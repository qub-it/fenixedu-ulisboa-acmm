package org.fenixedu.accessControl.ui.profiles.frontOffice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.accessControl.ui.profiles.ProfilesController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Controller
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Navigation.frontoffice")
@RequestMapping("front-office-navigationProfile")
public class NavigationProfileFO {

    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model, @RequestParam AcademicOperationType operation) {

        final Map<String, String> users = getMembers(operation);

        final Set<User> usersList = Bennu.getInstance().getUserSet();

        final Map<String, Map<String, String>> functionalities = new HashMap<>();

        model.addAttribute("operation", operation);
        model.addAttribute("users", users);
        model.addAttribute("usersList", usersList);
        model.addAttribute("functionalities", functionalities);

        return "profiles/backOffice/navigation/auths";
    }

    @Atomic(mode = TxMode.WRITE)
    private Map<String, String> getMembers(AcademicOperationType operation) {

        final Map<String, String> users = new HashMap<>();
        AcademicAccessRule.getMembers(operation, null, null).forEach(user -> {
            users.put(user.getName(), user.getExternalId());
        });

        return users;

    }

    @RequestMapping(path = "getTree", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    @ResponseBody
    public String getTree(@RequestParam String operation) {

        final Gson json = new Gson();

        return json
                .toJson(getMenus(PortalConfiguration.getInstance().getMenu().getAsMenuContainer().getOrderedChild(), operation));

    }

    private Set<Object> getMenus(Set<MenuItem> menus, String operation) {

        final Set<Object> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.getAccessGroup().getExpression().contains(operation)) {
                if (menuItem.isMenuContainer()) {

                    final Map<String, Object> folder = new HashMap<>();

                    folder.put("key", menuItem.getExternalId());

                    folder.put("title", menuItem.getTitle().getContent());
                    folder.put("folder", "true");
                    folder.put("expanded", "true");

                    final Set<MenuItem> submenus = menuItem.getAsMenuContainer().getOrderedChild();
                    folder.put("children", getMenus(submenus, operation));

                    items.add(folder);

                } else {

                    final Map<String, String> leaf = new HashMap<>();

                    leaf.put("key", menuItem.getExternalId());
                    leaf.put("title", menuItem.getTitle().getContent());

                    items.add(leaf);
                }
            }
        }
        return items;
    }

}
