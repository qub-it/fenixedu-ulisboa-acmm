package org.fenixedu.accessControl.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ImportProfiles extends CustomTask {

    public class ProfileBean {

        String name;
        String cod;
        String type;
        Set<AcademicOperationType> auths;
        Set<String> parents;
        Set<String> menus;

        public ProfileBean(String name, String cod, String type, Set<AcademicOperationType> auths, Set<String> parents,
                Set<String> menus) {
            this.name = name;
            this.cod = cod;
            this.type = type;
            this.auths = auths;
            this.parents = parents;
            this.menus = menus;
        }

        public Set<AcademicOperationType> getAuths() {
            return auths;
        }

        public Set<String> getMenus() {
            return menus;
        }

        public Set<String> getParents() {
            return parents;
        }

        public String getName() {
            return name;
        }

        public String getCod() {
            return cod;
        }

        public String getType() {
            return type;
        }

    }

    @Override
    public void runTask() throws Exception {

        final File file = new File("/tmp/exportedProfiles.json");

        @SuppressWarnings("resource")
        final BufferedReader br = new BufferedReader(new FileReader(file));

        final String json = br.readLine();

//        final String json =
//                "[{\"name\":\"Teste\",\"cod\":\"tata\",\"type\":\"Managers\",\"auths\":[],\"parents\":[\"t170\"],\"menus\":[]},{\"name\":\"test2\",\"cod\":\"t170\",\"type\":\"Managers\",\"auths\":[\"MANAGE_AUTHORIZATIONS\"],\"parents\":[],\"menus\":[\"/system-administration/profiles\",\"/system-administration\",\"/system-administration/profiles/front-office-navigationProfile\"]}]";
        final Gson gson = new Gson();

        final Set<ProfileBean> profiles = gson.fromJson(json, new TypeToken<Set<ProfileBean>>() {
        }.getType());

        profiles.forEach(profile -> {

            final ProfileGroup prfg = new ProfileGroup(profile.getCod());

            prfg.setName(profile.getName());

            prfg.setType(profile.getType());

            profile.getAuths().forEach(auth -> {
                final Set<AcademicAccessTarget> targets = new HashSet<>();
                try {
                    new AcademicAccessRule(auth, prfg, targets);
                } catch (final Exception e) {
                    taskLog(e.getMessage());
                }

            });

            setMenus(PortalConfiguration.getInstance().getMenu().getAsMenuContainer().getOrderedChild(), prfg,
                    profile.getMenus());

        });

        profiles.forEach(profile -> {

            final ProfileGroup prfg = new ProfileGroup(profile.getCod());

            profile.getParents().forEach(parent -> {
                prfg.addParent(new ProfileGroup(parent));
            });
        });
    }

    private void setMenus(Set<MenuItem> menus, ProfileGroup profile, Set<String> paths) {

        for (final MenuItem menuItem : menus) {
            if (paths.contains(menuItem.getFullPath())) {
                menuItem.setAccessGroup(menuItem.getAccessGroup().or(profile));
                if (menuItem.isMenuContainer()) {
                    setMenus(menuItem.getAsMenuContainer().getOrderedChild(), profile, paths);
                }
            }
        }
    }
}
