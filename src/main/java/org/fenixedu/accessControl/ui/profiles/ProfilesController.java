package org.fenixedu.accessControl.ui.profiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.phd.PhdProgram;
import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.SpringApplication;
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

@RequestMapping("/profiles")
@SpringApplication(group = "academic(MANAGE_AUTHORIZATIONS)", path = "profiles", title = "title.Accesscontrol.Profiles",
        hint = "Access Control")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles")
public class ProfilesController {

    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model) {

        final Set<PersistentProfileGroup> profiles = Bennu.getInstance().getProfileGroupSet();
        final AcademicOperationType[] operations = AcademicOperationType.class.getEnumConstants();
        final Set<User> users = Bennu.getInstance().getUserSet();

        final Map<String, Set<User>> profilesUsers = new HashMap<>();

        final Multimap<String, AcademicAccessRule> profilesAuths = HashMultimap.create();

        profiles.forEach(profile -> {
            profilesUsers.put(profile.getExternalId(), profile.getMembersWithoutParents().collect(Collectors.toSet()));
        });

        AcademicAccessRule.accessRules().forEach(rule -> {
            if (rule.getWhoCanAccess() instanceof ProfileGroup) {
                profilesAuths.put(((ProfileGroup) rule.getWhoCanAccess()).toPersistentGroup().getExternalId(), rule);
            }
        });

        final Set<AdministrativeOffice> offices = Bennu.getInstance().getAdministrativeOfficesSet();
        final Set<Degree> degrees = Bennu.getInstance().getDegreesSet();
        final Set<PhdProgram> phdPrograms = Bennu.getInstance().getPhdProgramsSet();

        model.addAttribute("profiles", profiles);
        model.addAttribute("profilesUsers", profilesUsers);
        model.addAttribute("profilesAuths", profilesAuths);
        model.addAttribute("operations", operations);
        model.addAttribute("offices", offices);
        model.addAttribute("degrees", degrees);
        model.addAttribute("phdPrograms", phdPrograms);

        model.addAttribute("users", users);
        return "profiles/profiles";
    }

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public String create(@RequestParam String name) {

        if (name.length() > 0) {
            createProfile(name);
        }

        return "redirect:/access-control/profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    private ProfileGroup createProfile(String name) {
        return new ProfileGroup(name);
    }

    @RequestMapping(path = "addAuth", method = RequestMethod.POST)
    @ResponseBody
    public String addAuth(@RequestParam PersistentProfileGroup profile, @RequestParam AcademicOperationType operation) {

        return addAuth((ProfileGroup) profile.toGroup(), operation, new DateTime("9999-12-31"));

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

    @RequestMapping(path = "addMember", method = RequestMethod.POST)
    @ResponseBody
    public String addMember(@RequestParam PersistentProfileGroup profile, @RequestParam String username) {

        final User user = User.findByUsername(username);

        addMember(profile.toGroup(), user);

        return user.getExternalId();
    }

    @Atomic(mode = TxMode.WRITE)
    private void addMember(Group profile, User user) {
        profile.grant(user);
    }

    @RequestMapping(path = "removeMember", method = RequestMethod.POST)
    @ResponseBody
    public String removeMember(@RequestParam PersistentProfileGroup profile, @RequestParam User user) {

        removeMember(profile.toGroup(), user);

        return "";
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeMember(Group profile, User user) {
        profile.revoke(user);
    }

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

}
