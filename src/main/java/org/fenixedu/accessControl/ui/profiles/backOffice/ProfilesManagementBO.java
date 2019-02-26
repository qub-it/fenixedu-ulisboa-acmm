package org.fenixedu.accessControl.ui.profiles.backOffice;

import java.util.HashSet;
import java.util.Set;

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
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@RequestMapping("back-office-profiles")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles.backoffice")
public class ProfilesManagementBO {
    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model) {

        final Set<PersistentProfileGroup> profiles = Bennu.getInstance().getProfileGroupSet();
        final AcademicOperationType[] operations = AcademicOperationType.class.getEnumConstants();

        final Set<ProfileType> types = Bennu.getInstance().getProfileTypeSet();

        final Multimap<String, AcademicAccessRule> profilesAuths = HashMultimap.create();

        final Multimap<String, String> authsMenus = HashMultimap.create();

        final Multimap<String, MenuItem> profilesMenus = HashMultimap.create();

        AcademicAccessRule.accessRules().forEach(rule -> {
            if (rule.getWhoCanAccess() instanceof ProfileGroup) {
                profilesAuths.put(((ProfileGroup) rule.getWhoCanAccess()).toPersistentGroup().getExternalId(), rule);

            }
        });

        getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild()).forEach(menu -> {

            final String[] groups = menu.getAccessGroup().getExpression().split("([|&-])");

            for (final String group : groups) {

                final Group parsed = Group.parse(group);
                if (parsed instanceof ProfileGroup) {
                    profilesMenus.put(parsed.toPersistentGroup().getExternalId(), menu);
                } else if (parsed instanceof AcademicAuthorizationGroup) {
                    authsMenus.put(parsed.getExpression(), menu.getFullPath());
                }

            }

        });

        final Set<AdministrativeOffice> offices = Bennu.getInstance().getAdministrativeOfficesSet();
        final Set<Degree> degrees = Bennu.getInstance().getDegreesSet();

        final Set<MenuItem> menus = getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild());

        model.addAttribute("profiles", profiles);
        model.addAttribute("profilesAuths", profilesAuths);
        model.addAttribute("profilesMenus", profilesMenus);
        model.addAttribute("authsMenus", authsMenus);
        model.addAttribute("operations", operations);
        model.addAttribute("offices", offices);
        model.addAttribute("degrees", degrees);
        model.addAttribute("menus", menus);
        model.addAttribute("types", types);

        return "profiles/backOffice/profiles/profiles";
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

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public String create(@RequestParam String name, @RequestParam String type) {

        if (name.length() > 0) {
            createProfile(name, type);
        }

        return "redirect:/access-control/profiles/back-office-profiles";
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

        return new AcademicAccessRule(operation, profile, targets, validity).getExternalId();
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

    @RequestMapping(path = "addToMenu", method = RequestMethod.POST)
    @ResponseBody
    public String addToMenu(@RequestParam PersistentProfileGroup profile, @RequestParam MenuItem menu) {

        final Group group = menu.getAccessGroup().or(profile.toGroup());

        setGroup(menu, group);

        return menu.getExternalId();
    }

    @RequestMapping(path = "removeFromMenu", method = RequestMethod.POST)
    @ResponseBody
    public String removeFromMenu(@RequestParam PersistentProfileGroup profile, @RequestParam MenuItem menu) {

        final Group group = Group.parse(menu.getAccessGroup().getExpression().replace(profile.toGroup().getExpression(), "nobody")
                .replace(profile.toGroup().getExpression(), "nobody"));

        setGroup(menu, group);

        return menu.getExternalId();
    }

    @Atomic(mode = TxMode.WRITE)
    private void setGroup(MenuItem menuItem, Group group) {
        menuItem.setAccessGroup(group);

        if (!menuItem.getParent().isRoot()) {
            setGroup(menuItem.getParent(), group);
        }
    }

//    @RequestMapping(path = "addMember", method = RequestMethod.POST)
//    @ResponseBody
//    public String addMember(@RequestParam PersistentProfileGroup profile, @RequestParam String username) {
//
//        final User user = User.findByUsername(username);
//
//        addMember(profile.toGroup(), user);
//
//        return user.getExternalId();
//    }
//
//    @Atomic(mode = TxMode.WRITE)
//    private void addMember(Group profile, User user) {
//        profile.grant(user);
//    }
//
//    @RequestMapping(path = "removeMember", method = RequestMethod.POST)
//    @ResponseBody
//    public String removeMember(@RequestParam PersistentProfileGroup profile, @RequestParam User user) {
//
//        removeMember(profile.toGroup(), user);
//
//        return "";
//    }
//
//    @Atomic(mode = TxMode.WRITE)
//    private void removeMember(Group profile, User user) {
//        profile.revoke(user);
//    }

    @RequestMapping(path = "delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestParam PersistentProfileGroup profile) {

        deleteprofile(profile);

        return "";
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
                setGroup(menu, grpTo);
            }
        });

        return "redirect:";
    }

    @Atomic(mode = TxMode.WRITE)
    private void crearteRule(AcademicAccessRule rule, ProfileGroup group) {
        new AcademicAccessRule(rule.getOperation(), group, rule.getWhatCanAffect(), rule.getValidity());
    }
}
