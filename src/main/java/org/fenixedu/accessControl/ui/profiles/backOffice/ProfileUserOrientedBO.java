package org.fenixedu.accessControl.ui.profiles.backOffice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.accessControl.ui.profiles.ProfilesController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@RequestMapping("back-office-users")
@SpringFunctionality(app = ProfilesController.class, title = "label.users.backoffice")
public class ProfileUserOrientedBO {

    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model) {

        final Set<User> users = Bennu.getInstance().getUserSet();
        final Set<PersistentProfileGroup> profileSet = getProfiles();

        model.addAttribute("users", users);
        model.addAttribute("profileSet", profileSet);

        return "profiles/backOffice/users/search";
    }

    @RequestMapping(path = "search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam String username) {

        final User user = User.findByUsername(username.split(" - ")[0]);
        final Set<PersistentProfileGroup> profiles = getProfiles(user);

        final Set<User> users = Bennu.getInstance().getUserSet();
        final Set<PersistentProfileGroup> profileSet = getProfiles();

        model.addAttribute("user", user);
        model.addAttribute("profiles", profiles);
        model.addAttribute("users", users);
        model.addAttribute("profileSet", profileSet);

        return "profiles/backOffice/users/search";
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

        final ProfileGroup group = new ProfileGroup(generateShort(profile));

        group.grant(user);

        return "";
    }

    private String generateShort(String name) {

        String cod = "";
        final String[] words = name.split(" ");

        for (final String word : words) {
            cod += word.charAt(0);
        }

        cod += String.valueOf(name.length());

        return cod;

    }

    @RequestMapping(path = "removeFromProfile", method = RequestMethod.POST)
    @ResponseBody
    public String removeFromProfile(@RequestParam User user, @RequestParam String profile) {

        final ProfileGroup group = new ProfileGroup(generateShort(profile));

        group.revoke(user);

        return "";
    }

    @RequestMapping(path = "copy", method = RequestMethod.GET)
    public String copy(Model model, @RequestParam String usernameTo, @RequestParam String usernameFrom) {

        final User userTo = User.findByUsername(usernameTo);
        final User userFrom = User.findByUsername(usernameFrom.split(" - ")[0]);

        Bennu.getInstance().getProfileGroupSet().forEach(profile -> {

            final ProfileGroup group = profile.toGroup();

            if (group.isMemberWithoutParents(userFrom) && !group.isMemberWithoutParents(userTo)) {
                group.grant(userTo);
            }

        });

        return "redirect:search?username=" + userTo.getUsername();
    }

    @RequestMapping(path = "getTree", method = RequestMethod.GET, produces = { "application/json; charset=UTF-8" })
    @ResponseBody
    public String getTree(@RequestParam User user) {

        final Gson json = new Gson();

        return json.toJson(getMenus(PortalConfiguration.getInstance().getMenu().getAsMenuContainer().getOrderedChild(), user));

    }

    private Set<Object> getMenus(Set<MenuItem> menus, User user) {

        final Set<Object> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.getAccessGroup().isMember(user)) {
                if (menuItem.isMenuContainer()) {

                    final Map<String, Object> folder = new HashMap<>();

                    folder.put("key", menuItem.getExternalId());
                    folder.put("title", menuItem.getTitle().getContent());
                    folder.put("folder", "true");
                    folder.put("expanded", "true");

                    final Set<MenuItem> submenus = menuItem.getAsMenuContainer().getOrderedChild();
                    folder.put("children", getMenus(submenus, user));

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