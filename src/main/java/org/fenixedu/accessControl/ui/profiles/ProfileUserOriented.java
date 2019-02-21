package org.fenixedu.accessControl.ui.profiles;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/users")
@SpringFunctionality(app = ProfilesController.class, title = "label.users")
public class ProfileUserOriented {

    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model) {

        final Set<String> users = getUsers();
        final Set<PersistentProfileGroup> profileSet = getProfiles();

        System.out.println("AQUI");

        model.addAttribute("users", users);
        model.addAttribute("profileSet", profileSet);

        return "profiles/users/search";
    }

    @RequestMapping(path = "search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam String username) {

        final User user = User.findByUsername(username);
        final Set<PersistentProfileGroup> profiles = getProfiles(user);

        final Set<String> users = getUsers();
        final Set<PersistentProfileGroup> profileSet = getProfiles();

        model.addAttribute("user", user);
        model.addAttribute("profiles", profiles);
        model.addAttribute("users", users);
        model.addAttribute("profileSet", profileSet);

        return "profiles/users/search";
    }

    private Set<String> getUsers() {

        final Set<String> users = new HashSet<String>();

        Bennu.getInstance().getUserSet().forEach(user -> {
            users.add(user.getUsername());
        });

        return users;
    }

    private Set<PersistentProfileGroup> getProfiles(User user) {

        return Bennu.getInstance().getProfileGroupSet().stream().filter(profile -> profile.isMember(user))
                .collect(Collectors.toSet());

    }

    private Set<PersistentProfileGroup> getProfiles() {

        return Bennu.getInstance().getProfileGroupSet();

    }

    @RequestMapping(path = "addToProfile", method = RequestMethod.POST)
    @ResponseBody
    public String addToProfile(@RequestParam User user, @RequestParam String profile) {

        final ProfileGroup group = new ProfileGroup(profile);

        group.grant(user);

        return "";
    }

    @RequestMapping(path = "removeFromProfile", method = RequestMethod.POST)
    @ResponseBody
    public String removeFromProfile(@RequestParam User user, @RequestParam String profile) {

        final ProfileGroup group = new ProfileGroup(profile);

        group.revoke(user);

        return "";
    }

}
