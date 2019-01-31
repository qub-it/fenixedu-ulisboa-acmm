package org.fenixedu.accessControl.ui.authorizations;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@RequestMapping("/dynamic-groups")
@SpringFunctionality(app = AuthorizationsController.class, title = "title.Accesscontrol.Authorizations.DynamicGroups")
public class DynamicGroups {
    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model) {

        final Set<String> users = getUsers();
        final Set<PersistentGroup> dynamicGroups = getDynamicGroups();

        model.addAttribute("users", users);
        model.addAttribute("dynamicGroups", dynamicGroups);

        return "authorizations/dynamicGroups/search";
    }

    @RequestMapping(path = "search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam String username) {

        final User user = User.findByUsername(username);
        final Set<PersistentGroup> groups = getDynamicGroups(user);
        final Set<String> users = getUsers();
        final Set<PersistentGroup> dynamicGroups = getDynamicGroups();

        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("groups", groups);
        model.addAttribute("dynamicGroups", dynamicGroups);

        return "authorizations/dynamicGroups/search";
    }

    @RequestMapping(path = "search/copy", method = RequestMethod.GET)
    public String copy(Model model, @RequestParam String username, @RequestParam String copyFromUsername) {

        final User user = User.findByUsername(username);
        final User copyFrom = User.findByUsername(copyFromUsername);

        final Set<PersistentGroup> groupsToCopy = getDynamicGroups(copyFrom);

        groupsToCopy.forEach(group -> {

            if (!group.isMember(user)) {
                addToGroup((DynamicGroup) group.toGroup(), user);
            }

        });

        return "redirect:?username=" + user.getUsername();
    }

    private Set<PersistentGroup> getDynamicGroups(User user) {
        final Set<PersistentGroup> groups = Bennu.getInstance().getGroupSet().stream()
                .filter(group -> (group.getClass().equals(PersistentDynamicGroup.class) && group.isMember(user)))
                .collect(Collectors.toSet());
        return groups;
    }

    private Set<PersistentGroup> getDynamicGroups() {
        final Set<PersistentGroup> groups = Bennu.getInstance().getGroupSet().stream()
                .filter(group -> (group.getClass().equals(PersistentDynamicGroup.class))).collect(Collectors.toSet());
        return groups;
    }

    private Set<String> getUsers() {

        final Set<String> users = new HashSet<String>();

        Bennu.getInstance().getUserSet().forEach(user -> {
            users.add(user.getUsername());
        });

        return users;
    }

    @RequestMapping(path = "add", method = RequestMethod.POST)
    @ResponseBody
    public Boolean addGroup(Model model, @RequestParam User user, @RequestParam PersistentDynamicGroup group) {

        final DynamicGroup dynamic = (DynamicGroup) group.toGroup();

        addToGroup(dynamic, user);

        return true;
    }

    @Atomic(mode = TxMode.WRITE)
    private void addToGroup(DynamicGroup group, User user) {

        group.mutator().changeGroup(group.underlyingGroup().grant(user));

    }

    @RequestMapping(path = "revoke", method = RequestMethod.POST)
    @ResponseBody
    public Boolean revokeGroup(Model model, @RequestParam User user, @RequestParam PersistentDynamicGroup group) {

        final DynamicGroup dynamic = (DynamicGroup) group.toGroup();

        removeFromGroup(dynamic, user);

        return true;
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeFromGroup(DynamicGroup group, User user) {

        group.mutator().changeGroup(group.underlyingGroup().revoke(user));

    }

}
