package org.fenixedu.accessControl.task;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import com.google.gson.Gson;

public class ExportProfiles extends CustomTask {

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

        public Set<String> getParents() {
            return parents;
        }

        public Set<String> getMenus() {
            return menus;
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

        final Set<ProfileBean> profiles = new HashSet<>();

        Bennu.getInstance().getProfileGroupSet().forEach(profile -> {

            final ProfileGroup prf = profile.toGroup();

            final Set<AcademicOperationType> auths = new HashSet<>();

            AcademicAccessRule.accessRules().forEach(auth -> {
                if (auth.getWhoCanAccess().equals(prf)) {
                    auths.add(auth.getOperation());
                }

            });

            final Set<String> parents = new HashSet<>();

            prf.getParentSet().forEach(parent -> {
                parents.add(parent.toGroup().getCod());
            });

            final Set<String> menus =
                    getMenus(PortalConfiguration.getInstance().getMenu().getAsMenuContainer().getOrderedChild(), profile);

            profiles.add(new ProfileBean(prf.getName(), prf.getCod(), prf.getType().getType(), auths, parents, menus));
        });

        final Gson gson = new Gson();

        final PrintWriter writer = new PrintWriter("/tmp/exportedProfiles.json", "UTF-8");
        writer.println(gson.toJson(profiles).toString());
        writer.close();

    }

    private Set<String> getMenus(Set<MenuItem> menus, PersistentProfileGroup profile) {

        final Set<String> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.getAccessGroup().getExpression().contains(profile.expression())) {
                if (menuItem.isMenuContainer()) {

                    final Set<MenuItem> submenus = menuItem.getAsMenuContainer().getOrderedChild();

                    items.add(menuItem.getFullPath());
                    items.addAll(getMenus(submenus, profile));

                } else {

                    items.add(menuItem.getFullPath());
                }
            }
        }
        return items;
    }

}
