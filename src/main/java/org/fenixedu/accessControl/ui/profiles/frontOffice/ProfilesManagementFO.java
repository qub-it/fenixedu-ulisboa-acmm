package org.fenixedu.accessControl.ui.profiles.frontOffice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.domain.groups.ProfileType;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.accessControl.ui.profiles.ProfilesController;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@RequestMapping("front-office-profiles")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles.frontoffice")
public class ProfilesManagementFO {
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model) {

        final Set<PersistentProfileGroup> profiles = Bennu.getInstance().getProfileGroupSet().stream()
                .filter(profile -> !profile.getType().equals(ProfileType.get("Managers"))).collect(Collectors.toSet());
        final AcademicOperationType[] operations = AcademicOperationType.class.getEnumConstants();

        final Set<ProfileType> types = Bennu.getInstance().getProfileTypeSet();
        final Set<User> users = Bennu.getInstance().getUserSet();

        final Multimap<String, AcademicAccessRule> profilesAuths = HashMultimap.create();

        final Multimap<String, String> authsMenus = HashMultimap.create();

        final Map<String, Set<User>> profilesUsers = new HashMap<>();

        final Map<String, Set<PersistentProfileGroup>> subProfiles = new HashMap<>();

        AcademicAccessRule.accessRules().forEach(rule -> {
            if (rule.getWhoCanAccess() instanceof ProfileGroup) {
                profilesAuths.put(((ProfileGroup) rule.getWhoCanAccess()).toPersistentGroup().getExternalId(), rule);
            }
        });

        getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).forEach(menu -> {
            final String[] groups = menu.getAccessGroup().getExpression().split("([|&-])");
            for (final String group : groups) {
                try {
                    final Group parsed = Group.parse(group);
                    if (parsed instanceof AcademicAuthorizationGroup) {
                        authsMenus.put(parsed.getExpression().replace("academic(", "").replace(")", ""),
                                menu.getTitle().getContent());
                    }
                } catch (final Exception e) {
                    System.out.println(e);
                }

            }
        });

        profiles.forEach(profile -> {
            profilesUsers.put(profile.getExternalId(), profile.getMembersWithoutParents().collect(Collectors.toSet()));
            subProfiles.put(profile.getExternalId(), profile.getChildSet().stream()
                    .filter(prf -> !prf.getType().equals(ProfileType.get("Managers"))).collect(Collectors.toSet()));
        });

        final Set<AdministrativeOffice> offices = Bennu.getInstance().getAdministrativeOfficesSet();
        final Set<Degree> degrees = Bennu.getInstance().getDegreesSet();

        model.addAttribute("profiles", profiles);
        model.addAttribute("profilesAuths", profilesAuths);
        model.addAttribute("subProfiles", subProfiles);
        model.addAttribute("profilesUsers", profilesUsers);
        model.addAttribute("authsMenus", authsMenus);
        model.addAttribute("operations", operations);
        model.addAttribute("offices", offices);
        model.addAttribute("degrees", degrees);
        model.addAttribute("types", types);
        model.addAttribute("users", users);

        return "profiles/frontOffice/profiles/profiles";
    }

    @RequestMapping(path = "getTree", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTree(@RequestParam PersistentProfileGroup profile) {

        final Gson json = new Gson();

        return json.toJson(getMenus(PortalConfiguration.getInstance().getMenu().getAsMenuContainer().getOrderedChild(), profile));

    }

    private Set<MenuItem> getMenu(Set<MenuItem> menus) {

        final Set<MenuItem> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.isMenuContainer()) {
                final Set<MenuItem> submenus = menuItem.getAsMenuContainer().getOrderedChild();
                items.addAll(getMenu(submenus));
                items.add(menuItem);
            } else {
                items.add(menuItem);
            }
        }
        return items;
    }

    private boolean checkAuthorizationGroups(MenuItem menu, PersistentProfileGroup profile) {

        boolean cond = false;

        final Set<AcademicAccessRule> rules = AcademicAccessRule.accessRules()
                .filter(rule -> rule.getWhoCanAccess().equals(profile.toGroup())).collect(Collectors.toSet());

        final String[] groups = menu.getAccessGroup().getExpression().split("([|&-])");

        for (final String group : groups) {

            try {
                final Group parsed = Group.parse(group);

                if (parsed instanceof AcademicAuthorizationGroup) {

                    for (final AcademicAccessRule rule : rules) {

                        if (AcademicAuthorizationGroup.get(rule.getOperation()).equals(parsed)) {

                            cond = true;

                        }

                    }

                }
            } catch (final Exception e) {
                System.out.println(e);
            }

        }

        return cond;
    }

    private Set<Object> getMenus(Set<MenuItem> menus, PersistentProfileGroup profile) {

        final Set<Object> items = new HashSet<>();
        for (final MenuItem menuItem : menus) {
            if (menuItem.getAccessGroup().getExpression().contains(profile.expression())
                    || checkAuthorizationGroups(menuItem, profile)) {
                if (menuItem.isMenuContainer()) {

                    final Map<String, Object> folder = new HashMap<>();

                    folder.put("key", menuItem.getExternalId());
                    folder.put("title", menuItem.getTitle().getContent());
                    folder.put("folder", "true");
                    folder.put("expanded", "true");

                    final Set<MenuItem> submenus = menuItem.getAsMenuContainer().getOrderedChild();
                    folder.put("children", getMenus(submenus, profile));

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

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public String create(@RequestParam String name) {

        if (name.length() > 0) {
            createProfile(name, "General");
        }

        return "redirect:";
    }

    @Atomic(mode = TxMode.WRITE)
    private ProfileGroup createProfile(String name, String type) {
        final ProfileGroup profile = new ProfileGroup(name);
        profile.setType(type);
        return profile;
    }

    @RequestMapping(path = "addAuth", method = RequestMethod.POST)
    @ResponseBody
    public String addAuth(@RequestParam PersistentProfileGroup profile, @RequestParam AcademicOperationType operation) {

        return addAuth(profile.toGroup(), operation, new DateTime("9999-12-31"));

    }

    @Atomic(mode = TxMode.WRITE)
    private String addAuth(ProfileGroup profile, AcademicOperationType operation, DateTime validity) {
        final Set<AcademicAccessTarget> targets = new HashSet<>();
//        return new AcademicAccessRule(operation, profile, targets, validity).getExternalId();
        return new AcademicAccessRule(operation, profile, targets).getExternalId();

    }

    @RequestMapping(path = "removeAuth", method = RequestMethod.POST)
    @ResponseBody
    public String removeAuth(@RequestParam AcademicAccessRule rule) {
        revokeAuth(rule);
        return "";

    }

    @Atomic(mode = TxMode.WRITE)
    private void revokeAuth(AcademicAccessRule rule) {
        rule.revoke();
    }

    @Atomic(mode = TxMode.WRITE)
    private void setGroup(MenuItem menuItem, Group group) {
        menuItem.setAccessGroup(group);

        if (!menuItem.getParent().isRoot()) {
            setGroup(menuItem.getParent(), group);
        }
    }

    @RequestMapping(path = "addMember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addMember(@RequestParam PersistentProfileGroup profile, @RequestParam String username) {

        if (profile.getType().equals(ProfileType.get("General"))) {
            final User user = User.findByUsername(username);
            addMember(profile.toGroup(), user);
            return new ResponseEntity<String>(user.getExternalId(), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void addMember(Group profile, User user) {
        profile.grant(user);
    }

    @RequestMapping(path = "removeMember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> removeMember(@RequestParam PersistentProfileGroup profile, @RequestParam User user) {

        if (profile.getType().equals(ProfileType.get("General"))) {
            removeMember(profile.toGroup(), user);
            return new ResponseEntity<String>("", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void removeMember(Group profile, User user) {
        profile.revoke(user);
    }

    @RequestMapping(path = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> delete(@RequestParam PersistentProfileGroup profile) {

        if (profile.getType().equals(ProfileType.get("General"))) {
            deleteprofile(profile);
            return new ResponseEntity<String>("", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void deleteprofile(PersistentProfileGroup profile) {

        profile.delete();

    }

    @RequestMapping(path = "modifyOffice", method = RequestMethod.POST)
    @ResponseBody
    public String editAuthorizationOffice(@RequestParam AcademicAccessRule rule, @RequestParam AdministrativeOffice scope,
            @RequestParam String action) {

        final Set<AdministrativeOffice> offices = rule.getOfficeSet();

        if (action.equals("add")) {
            addOffice(scope, offices);
        } else {
            removeOffice(scope, offices);
        }

        return scope.getExternalId();

    }

    @Atomic(mode = TxMode.WRITE)
    private void addOffice(AdministrativeOffice office, Set<AdministrativeOffice> offices) {
        offices.add(office);
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeOffice(AdministrativeOffice office, Set<AdministrativeOffice> offices) {
        offices.remove(office);
    }

    @RequestMapping(path = "modifyProgram", method = RequestMethod.POST)
    @ResponseBody
    public String editAuthorizationProgram(@RequestParam AcademicAccessRule rule, @RequestParam AcademicProgram scope,
            @RequestParam String action) {

        final Set<AcademicProgram> programs = rule.getProgramSet();

        if (action.equals("add")) {
            addProgram(scope, programs);
        } else {
            removeProgram(scope, programs);
        }

        return scope.getExternalId();

    }

    @Atomic(mode = TxMode.WRITE)
    private void addProgram(AcademicProgram program, Set<AcademicProgram> programs) {
        programs.add(program);
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeProgram(AcademicProgram program, Set<AcademicProgram> programs) {
        programs.remove(program);
    }

    @RequestMapping(path = "copy", method = RequestMethod.GET)
    public String accessGroupCopy(Model model, @RequestParam String groupFrom, @RequestParam String groupTo) {

        final ProfileGroup grpFrom = new ProfileGroup(groupFrom);
        final ProfileGroup grpTo = new ProfileGroup(groupTo);

        AcademicAccessRule.accessRules().forEach(rule -> {
            if (rule.getWhoCanAccess().equals(grpFrom)) {
                crearteRule(rule, grpTo);
            }
        });

        getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).forEach(menu -> {
            if (menu.getAccessGroup().getExpression().contains(groupFrom)) {
                setGroup(menu, grpFrom.or(grpTo));
            }
        });

        return "redirect:";
    }

    @Atomic(mode = TxMode.WRITE)
    private void crearteRule(AcademicAccessRule rule, ProfileGroup group) {
//        new AcademicAccessRule(rule.getOperation(), group, rule.getWhatCanAffect(), rule.getValidity());
        new AcademicAccessRule(rule.getOperation(), group, rule.getWhatCanAffect());

    }

    @RequestMapping(path = "addChild", method = RequestMethod.POST)
    @ResponseBody
    private String addChild(@RequestParam PersistentProfileGroup parent, @RequestParam PersistentProfileGroup child) {

        addToParent(parent.toGroup(), child.toGroup());
        return "";
    }

    @Atomic(mode = TxMode.WRITE)
    private void addToParent(ProfileGroup parent, ProfileGroup child) {
        child.addParent(parent);
    }

    @RequestMapping(path = "removeChild", method = RequestMethod.POST)
    @ResponseBody
    private String removeChild(@RequestParam PersistentProfileGroup parent, @RequestParam PersistentProfileGroup child) {

        removeFromParent(parent.toGroup(), child.toGroup());
        return "";
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeFromParent(ProfileGroup parent, ProfileGroup child) {
        child.removeParent(parent);
    }
}