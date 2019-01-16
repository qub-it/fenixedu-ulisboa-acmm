package org.fenixedu.accessControl.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.phd.PhdProgram;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@RequestMapping("/academic-authorizations")
@SpringFunctionality(app = AccesscontrolController.class, title = "title.academicAuthorizations.AcademicAuthorizations")
public class AcademicAuthorizations {
    @RequestMapping(method = RequestMethod.GET)
    public String initial(Model model) {

        final List<AcademicAccessRule> rules = new ArrayList<AcademicAccessRule>();
        final Set<String> users = getUsers();
        final AcademicOperationType[] operations = AcademicOperationType.class.getEnumConstants();
        final Set<AdministrativeOffice> offices = Bennu.getInstance().getAdministrativeOfficesSet();
        final Set<Degree> degrees = Bennu.getInstance().getDegreesSet();
        final Set<PhdProgram> phdPrograms = Bennu.getInstance().getPhdProgramsSet();

        model.addAttribute("rules", rules);
        model.addAttribute("users", users);
        model.addAttribute("operations", operations);
        model.addAttribute("offices", offices);
        model.addAttribute("degrees", degrees);
        model.addAttribute("phdPrograms", phdPrograms);

        return "academicAuthorizations/search";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam String username) {

        final User user = User.findByUsername(username);

        final Set<AcademicAccessRule> rules = getAuthorizations(user);

        final Set<String> users = getUsers();
        final AcademicOperationType[] operations = AcademicOperationType.class.getEnumConstants();
        final Set<AdministrativeOffice> offices = Bennu.getInstance().getAdministrativeOfficesSet();
        final Set<Degree> degrees = Bennu.getInstance().getDegreesSet();
        final Set<PhdProgram> phdPrograms = Bennu.getInstance().getPhdProgramsSet();

        model.addAttribute("user", user);
        model.addAttribute("rules", rules);
        model.addAttribute("users", users);
        model.addAttribute("operations", operations);
        model.addAttribute("offices", offices);
        model.addAttribute("degrees", degrees);
        model.addAttribute("phdPrograms", phdPrograms);

        return "academicAuthorizations/search";
    }

    @RequestMapping(value = "search/copy", method = RequestMethod.GET)
    public String copy(Model model, @RequestParam String username, @RequestParam String copyFromUsername) {

        final User user = User.findByUsername(username);
        final User copyFrom = User.findByUsername(copyFromUsername);

        final Set<AcademicAccessRule> rules = getAuthorizations(user);
        final Set<AcademicAccessRule> rulesToCopy = getAuthorizations(copyFrom);

        rulesToCopy.forEach(rule -> {

            rules.stream().forEach(ru -> ru.getOperation());

            if (rules.stream().noneMatch(r -> r.getOperation() == rule.getOperation())) {
                grantRule(rule.getOperation(), user, rule.getWhatCanAffect(), rule.getValidity());
            }

        });

        return "redirect:?username=" + user.getUsername();
    }

    @Atomic(mode = TxMode.WRITE)
    private Set<AcademicAccessRule> getAuthorizations(User user) {
        final Set<AcademicAccessRule> userAcademicOperation = new HashSet<>();

        AcademicAccessRule.accessRules().forEach(rule -> {
            rule.getWhoCanAccess().getMembers().forEach(u -> {
                if (u.equals(user)) {
                    userAcademicOperation.add(rule);
                }
            });
        });

        return userAcademicOperation;
    }

    private Set<String> getUsers() {

        final Set<String> users = new HashSet<String>();

        Bennu.getInstance().getUserSet().forEach(user -> {
            users.add(user.getUsername());
        });

        return users;
    }

    @RequestMapping(value = "addRule", method = RequestMethod.POST)
    @ResponseBody
    public String addRule(@RequestParam AcademicOperationType operation, @RequestParam User user, @RequestParam String validity) {

        if (validity.length() == 0) {
            validity = "9999-12-31";
        }

        final Set<AcademicAccessTarget> targets = new HashSet<AcademicAccessTarget>();
        final String id = grantRule(operation, user, targets, new DateTime(validity));

        return id;
    }

    @Atomic(mode = TxMode.WRITE)
    private String grantRule(AcademicOperationType operation, User user, Set<AcademicAccessTarget> targets, DateTime validity) {
        final AcademicAccessRule rule = new AcademicAccessRule(operation, user.groupOf(), targets, validity);
        return rule.getExternalId();
    }

    @RequestMapping(value = "revoke", method = RequestMethod.POST)
    @ResponseBody
    public String revokeRule(Model model, @RequestParam AcademicAccessRule rule) {

        revoke(rule);

        return "";
    }

    @Atomic(mode = TxMode.WRITE)
    private void revoke(AcademicAccessRule rule) {
        rule.revoke();
    }

    @RequestMapping(value = "modifyOffice", method = RequestMethod.POST)
    @ResponseBody
    public String editAuthorizationOffice(Model model, @RequestParam AcademicAccessRule rule,
            @RequestParam AdministrativeOffice scope, @RequestParam String action) {

        final Set<AdministrativeOffice> offices = rule.getOfficeSet();

        if (action.equals("add")) {
            addOffice(rule, scope, offices);
        } else {
            removeOffice(rule, scope, offices);
        }

        return scope.getExternalId();
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeOffice(AcademicAccessRule rule, AdministrativeOffice office, Set<AdministrativeOffice> offices) {
        offices.remove(office);
    }

    @Atomic(mode = TxMode.WRITE)
    private void addOffice(AcademicAccessRule rule, AdministrativeOffice office, Set<AdministrativeOffice> offices) {
        offices.add(office);
    }

    @RequestMapping(value = "modifyProgram", method = RequestMethod.POST)
    @ResponseBody
    public String editAuthorizationProgram(Model model, @RequestParam AcademicAccessRule rule,
            @RequestParam AcademicProgram scope, @RequestParam String action) {

        final Set<AcademicProgram> programs = rule.getProgramSet();

        if (action.equals("add")) {
            addProgram(rule, scope, programs);
        } else {
            removeProgram(rule, scope, programs);
        }

        return scope.getExternalId();
    }

    @Atomic(mode = TxMode.WRITE)
    private void removeProgram(AcademicAccessRule rule, AcademicProgram program, Set<AcademicProgram> programs) {
        programs.remove(program);
    }

    @Atomic(mode = TxMode.WRITE)
    private void addProgram(AcademicAccessRule rule, AcademicProgram program, Set<AcademicProgram> programs) {
        programs.add(program);
    }

    @RequestMapping(value = "modifyValidity", method = RequestMethod.POST)
    @ResponseBody
    public String editAuthorizationValidity(@RequestParam AcademicAccessRule rule, @RequestParam String validity) {

        final DateTime newValidity = new DateTime(validity);

        if (newValidity.isAfterNow()) {
            alterValidity(rule, newValidity);

            return rule.getExternalId();
        }

        return null;

    }

    @Atomic(mode = TxMode.WRITE)
    private void alterValidity(AcademicAccessRule rule, DateTime validity) {
        rule.setValidity(validity);
    }
}
