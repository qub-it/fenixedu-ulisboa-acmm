package org.fenixedu.accessControl.ui.authorizations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
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

        final Set<User> usersList = Bennu.getInstance().getUserSet();

        final Map<String, Map<String, String>> functionalities = new HashMap<>();

        final Set<MenuItem> menus = new HashSet<>();

        getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).forEach(functionality -> {
            if (functionality.getAccessGroup().getExpression().contains("(" + operation.name().toString() + ")")
                    && functionality.isVisible()
                    && !(functionality.getAccessGroup().getExpression().contains("&")
                            || functionality.getAccessGroup().getExpression().contains("-")
                            || functionality.getAccessGroup().getExpression().contains("!")

                    )

            ) {

                final Map<String, String> info = new HashMap<>();

                info.put("url", functionality.getFullPath());
                info.put("oid", functionality.getExternalId());
                info.put("expression", functionality.getAccessGroup().getExpression());

                functionalities.put(functionality.getTitle().getContent(), info);
            }

            if (functionality.isVisible()) {
                menus.add(functionality);
            }
        });

        model.addAttribute("operation", operation);
        model.addAttribute("users", users);
        model.addAttribute("usersList", usersList);
        model.addAttribute("functionalities", functionalities);
        model.addAttribute("menus", menus);

        return "authorizations/navigation/auths";
    }

    @Atomic(mode = TxMode.WRITE)
    private Map<String, String> getMembers(AcademicOperationType operation) {

        final Map<String, String> users = new HashMap<>();
        AcademicAccessRule.getMembers(operation, null, null).forEach(user -> {
            users.put(user.getName(), user.getExternalId());
        });

        return users;

    }

    @RequestMapping(path = "addUserToAuth", method = RequestMethod.POST)
    @ResponseBody
    public String addUserToAuth(@RequestParam AcademicOperationType operation, @RequestParam String username) {

        final User user = User.findByUsername(username);

        final Set<AcademicAccessTarget> targets = new HashSet<AcademicAccessTarget>();
        final String id = grantRule(operation, user, targets, new DateTime("9999-12-31"));

        return id;
    }

    @Atomic(mode = TxMode.WRITE)
    private String grantRule(AcademicOperationType operation, User user, Set<AcademicAccessTarget> targets, DateTime validity) {
//        final AcademicAccessRule rule = new AcademicAccessRule(operation, user.groupOf(), targets, validity);
        final AcademicAccessRule rule = new AcademicAccessRule(operation, user.groupOf(), targets);

        return rule.getExternalId();
    }

    @RequestMapping(path = "removeUserToAuth", method = RequestMethod.POST)
    @ResponseBody
    public String removeUserToAuth(@RequestParam AcademicOperationType operation, @RequestParam String username) {

        final User user = User.findByUsername(username);

        try {
            final AcademicAccessRule rule = AcademicAccessRule.accessRules()
                    .filter(r -> r.getOperation().equals(operation) && r.getWhoCanAccess().isMember(user)).findFirst().get();
            revokeRule(rule);
            return "";
        } catch (final Exception e) {
            throw new Error("Rule doesn't exists!");
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void revokeRule(AcademicAccessRule rule) {
        rule.revoke();
    }

    @RequestMapping(path = "addUserToGroup", method = RequestMethod.POST)
    @ResponseBody
    public Boolean addGroup(Model model, @RequestParam String username, @RequestParam String expression) {

        final User user = User.findByUsername(username);

        final DynamicGroup dynamic = (DynamicGroup) Group.parse(expression);

        addToGroup(dynamic, user);

        return true;
    }

    @Atomic(mode = TxMode.WRITE)
    private void addToGroup(DynamicGroup group, User user) {

        group.mutator().changeGroup(group.underlyingGroup().grant(user));

    }

    @RequestMapping(path = "removeUserToGroup", method = RequestMethod.POST)
    @ResponseBody
    public Boolean revokeGroup(Model model, @RequestParam String username, @RequestParam String expression) {

        final User user = User.findByUsername(username);

        final DynamicGroup dynamic = (DynamicGroup) Group.parse(expression);

        removeFromGroup(dynamic, user);

        return true;
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeFromGroup(DynamicGroup group, User user) {

        group.mutator().changeGroup(group.underlyingGroup().revoke(user));

    }

    @RequestMapping(path = "accessGroup", method = RequestMethod.GET)
    public String accessGroup(Model model, @RequestParam String expression) {

        final Group group = Group.parse(expression);

        final Set<User> users = group.getMembers().collect(Collectors.toSet());

        final Set<User> usersList = Bennu.getInstance().getUserSet();

        final Set<MenuItem> menusList = getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).stream()
                .filter(menu -> menu.isVisible()).collect(Collectors.toSet());

        final Set<MenuItem> menus = getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).stream()
                .filter(menu -> menu.getAccessGroup().getExpression().contains(expression) && menu.isVisible()
                        && !(menu.getAccessGroup().getExpression().contains("&")
                                || menu.getAccessGroup().getExpression().contains("-")
                                || menu.getAccessGroup().getExpression().contains("!")

                        )).collect(Collectors.toSet());

        model.addAttribute("expression", group.getExpression());
        model.addAttribute("users", users);
        model.addAttribute("usersList", usersList);
        model.addAttribute("menus", menus);
        model.addAttribute("menusList", menusList);

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

    @RequestMapping(path = "addMenuToAuth", method = RequestMethod.POST)
    @ResponseBody
    public String addMenuToAuth(@RequestParam MenuItem menuItem, @RequestParam AcademicOperationType operation) {

        final Group group = menuItem.getAccessGroup().or(Group.parse("academic(" + operation + ")"));

        setGroup(menuItem, group);

        return "";
    }

    @RequestMapping(path = "addMenuToGroup", method = RequestMethod.POST)
    @ResponseBody
    public String addMenuToGroup(@RequestParam MenuItem menuItem, @RequestParam String expression) {

        final Group group = menuItem.getAccessGroup().or(Group.parse(expression));

        setGroup(menuItem, group);

        return "";
    }

    @RequestMapping(path = "removeMenuToAuth", method = RequestMethod.POST)
    @ResponseBody
    public String removeMenuToAuth(@RequestParam MenuItem menuItem, @RequestParam AcademicOperationType operation) {

        final Group group = Group.parse(menuItem.getAccessGroup().getExpression().replace("academic(" + operation + ")", "nobody")
                .replace("academic(" + operation + ")", "nobody"));

        setGroup(menuItem, group);

        return "";
    }

    @RequestMapping(path = "removeMenuToGroup", method = RequestMethod.POST)
    @ResponseBody
    public String removeMenuToGroup(@RequestParam MenuItem menuItem, @RequestParam String expression) {

        final Group group = Group
                .parse(menuItem.getAccessGroup().getExpression().replace(expression, "nobody").replace(expression, "nobody"));

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
