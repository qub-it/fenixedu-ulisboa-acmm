package org.fenixedu.accessControl.ui.profiles;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.accessControl.domain.ProfileSC;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@RequestMapping("/profiles")
@SpringApplication(group = "academic(MANAGE_AUTHORIZATIONS)", path = "profiles", title = "title.Accesscontrol.Profiles",
        hint = "Access Control")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles")
public class ProfilesController {

    @RequestMapping(method = RequestMethod.GET)
    public String init(Model model) {

        final Set<ProfileSC> profiles = Bennu.getInstance().getProfilescSet();

        model.addAttribute("profiles", profiles);
        return "profiles/profiles";
    }

    @RequestMapping(path = "create", method = RequestMethod.GET)
    public String create(Model model, @RequestParam String name) {

        final ProfileSC profile = createProfile(name);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    private ProfileSC createProfile(String name) {
        return new ProfileSC(name);
    }

    @RequestMapping(path = "addGroup", method = RequestMethod.GET)
    public String addGroup(Model model, @RequestParam ProfileSC profile, @RequestParam PersistentGroup group) {

        addGroup(profile, group);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    private void addGroup(ProfileSC profile, PersistentGroup group) {
        profile.addGroup(group);
    }

    @RequestMapping(path = "removeGroup", method = RequestMethod.GET)
    public String removeGroup(Model model, @RequestParam ProfileSC profile, @RequestParam PersistentGroup group) {

        removeGroup(profile, group);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeGroup(ProfileSC profile, PersistentGroup group) {
        profile.removeGroup(group);
    }

    @RequestMapping(path = "addAuth", method = RequestMethod.GET)
    public String addAuth(Model model, @RequestParam ProfileSC profile, @RequestParam AcademicOperationType operation,
            @RequestParam String validity) {

        addAuth(profile, operation, new DateTime(validity));

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    private void addAuth(ProfileSC profile, AcademicOperationType operation, DateTime validity) {
        final Set<AcademicAccessTarget> targets = new HashSet<>();
        profile.addAuth(operation, targets, validity);
    }

    @RequestMapping(path = "removeAuth", method = RequestMethod.GET)
    public String removeAuth(Model model, @RequestParam ProfileSC profile, @RequestParam AcademicOperationType operation) {

        removeAuth(profile, operation);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    public void removeAuth(ProfileSC profile, AcademicOperationType operation) {
        profile.removeAuth(operation);
    }

    @RequestMapping(path = "addMember", method = RequestMethod.GET)
    public String addMember(Model model, @RequestParam ProfileSC profile, @RequestParam User user) {

        addMember(profile, user);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    public void addMember(ProfileSC profile, User user) {
        profile.addMember(user);
    }

    @RequestMapping(path = "removeMember", method = RequestMethod.GET)
    public String removeMember(Model model, @RequestParam ProfileSC profile, @RequestParam User user) {

        removeMember(profile, user);

        model.addAttribute("profile", profile);
        return "profiles/profiles";
    }

    @Atomic(mode = TxMode.WRITE)
    public void removeMember(ProfileSC profile, User user) {
        profile.removeMember(user);
    }

}
