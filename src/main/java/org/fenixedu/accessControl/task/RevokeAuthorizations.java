package org.fenixedu.accessControl.task;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

import pt.ist.fenixframework.Atomic;

@Task(englishTitle = "Revoke expired authorizations")
public class RevokeAuthorizations extends CronTask {

    @Override
    public void runTask() throws Exception {

        AcademicAccessRule.accessRules().forEach(rule -> {

//            if (rule.getValidity().isBeforeNow()) {
//                revoke(rule);
//            }

        });

    }

    @Atomic
    private void revoke(AcademicAccessRule rule) {
        rule.revoke();
        taskLog(rule.getExternalId() + " revoked");
    }

}
