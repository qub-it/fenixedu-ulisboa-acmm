package org.fenixedu.accessControl.ui.authorizations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Controller
@SpringFunctionality(app = AuthorizationsController.class, title = "title.Accesscontrol.Navigation")
@RequestMapping("/navigation")
public class Navigation {

    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model, @RequestParam AcademicOperationType operation) {

        final Map<String, String> users = getMembers(operation);

        final Map<String, Map<String, String>> functionalities = new HashMap<>();

        getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).forEach(functionality -> {
            if (functionality.getAccessGroup().getExpression().contains("(" + operation.name().toString() + ")")
                    && functionality.isVisible()) {

                final Map<String, String> info = new HashMap<>();

                info.put("url", functionality.getFullPath());
                info.put("oid", functionality.getExternalId());
                info.put("expression", functionality.getAccessGroup().getExpression());

                functionalities.put(functionality.getTitle().getContent(), info);
            }
        });

        model.addAttribute("operation", operation);
        model.addAttribute("users", users);
        model.addAttribute("functionalities", functionalities);

        return "authorizations/navigation/navigation";
    }

    @Atomic(mode = TxMode.WRITE)
    private Map<String, String> getMembers(AcademicOperationType operation) {

        final Map<String, String> users = new HashMap<>();
        AcademicAccessRule.getMembers(operation, null, null).forEach(user -> {
            users.put(user.getName(), user.getExternalId());
        });

        return users;

    }

    @RequestMapping(path = "addUser", method = RequestMethod.POST)
    @ResponseBody
    public String addUser(@RequestParam AcademicOperationType operation, @RequestParam User user) {

        final Set<AcademicAccessTarget> targets = new HashSet<AcademicAccessTarget>();
        final String id = grantRule(operation, user, targets, new DateTime("9999-12-31"));

        return id;
    }

    @Atomic(mode = TxMode.WRITE)
    private String grantRule(AcademicOperationType operation, User user, Set<AcademicAccessTarget> targets, DateTime validity) {
        final AcademicAccessRule rule = new AcademicAccessRule(operation, user.groupOf(), targets, validity);

        return rule.getExternalId();
    }

    @RequestMapping(path = "accessGroup", method = RequestMethod.GET)
    public String accessGroup(Model model, @RequestParam String expression) {

        final Group group = Group.parse(expression);

        final Set<User> users = group.getMembers().collect(Collectors.toSet());

        final Set<MenuItem> menus = getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).stream()
                .filter(menu -> menu.getAccessGroup().getExpression().contains(expression) && menu.isVisible())
                .collect(Collectors.toSet());

        model.addAttribute("expression", group.getExpression());
        model.addAttribute("users", users);
        model.addAttribute("menus", menus);

        return "authorizations/navigation/group";
    }

    private Set<MenuItem> getMenu(Set<MenuItem> menus) {

        final Set<MenuItem> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.isMenuContainer()) {
                final Set<MenuItem> submenus = ((MenuContainer) menuItem).getOrderedChild();
                items.addAll(getMenu(submenus));
            } else {
                items.add(menuItem);
            }
        }
        return items;
    }

    @RequestMapping(path = "addMenu", method = RequestMethod.GET)
    @ResponseBody
    public String addMenu(Model model, @RequestParam MenuItem menuItem, @RequestParam AcademicOperationType operation) {

        final Group group = menuItem.getAccessGroup().or(Group.parse("academic(" + operation + ")"));

        setGroup(menuItem, group);

        return "";
    }

    @Atomic(mode = TxMode.WRITE)
    private void setGroup(MenuItem menuItem, Group group) {
        menuItem.setAccessGroup(group);

        if (!menuItem.getParent().isRoot()) {
            setGroup(menuItem.getParent(), group);
        }
    }

}
