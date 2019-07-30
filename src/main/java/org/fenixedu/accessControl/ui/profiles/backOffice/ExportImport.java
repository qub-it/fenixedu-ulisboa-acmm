package org.fenixedu.accessControl.ui.profiles.backOffice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.accessControl.ui.profiles.ProfileBean;
import org.fenixedu.accessControl.ui.profiles.ProfilesController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Controller
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.exportImport")
@RequestMapping("export-import")
public class ExportImport {

    @RequestMapping(method = RequestMethod.GET)
    public String initial() {

        return "profiles/backOffice/exportImport/exportImport";
    }

    @RequestMapping(path = "export", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    public void export(HttpServletResponse response) {

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

        PrintWriter writer;
        try {
            writer = new PrintWriter("/tmp/exportedProfiles.json", "UTF-8");
            writer.println(gson.toJson(profiles).toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            final File initialFile = new File("/tmp/exportedProfiles.json");

            final InputStream is = new FileInputStream(initialFile);

            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (final IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

    @RequestMapping(path = "import", method = RequestMethod.POST)
    public String handleFile(@RequestParam("file") MultipartFile file) {

        try {

            final StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            createProfiles(textBuilder.toString());

        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "redirect:";
    }

    @Atomic(mode = TxMode.WRITE)
    public void createProfiles(String json) {

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
                    e.getMessage();
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
