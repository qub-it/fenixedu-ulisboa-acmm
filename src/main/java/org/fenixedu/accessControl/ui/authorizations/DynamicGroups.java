package org.fenixedu.accessControl.ui.authorizations;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/dynamic-groups")
@SpringFunctionality(app = AuthorizationsController.class, title = "title.Accesscontrol.Authorizations.DynamicGroups")
public class DynamicGroups {
    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model) {

        final Set<String> users = getUsers();

        model.addAttribute("users", users);
        return "authorizations/dynamicGroups/search";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam String username) {

        final User user = User.findByUsername(username);
        final Set<PersistentGroup> groups = getDynamicGroups(user);
        final Set<String> users = getUsers();

        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("groups", groups);

        return "authorizations/dynamicGroups/search";
    }

    private Set<PersistentGroup> getDynamicGroups(User user) {

        final Set<PersistentGroup> groups = Bennu.getInstance().getGroupSet().stream()
                .filter(group -> (group.getClass().equals(PersistentDynamicGroup.class) && group.isMember(user)))
                .collect(Collectors.toSet());

        return groups;

    }

    private Set<String> getUsers() {

        final Set<String> users = new HashSet<String>();

        Bennu.getInstance().getUserSet().forEach(user -> {
            users.add(user.getUsername());
        });

        return users;
    }

}
