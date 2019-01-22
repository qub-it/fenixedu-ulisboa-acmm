package org.fenixedu.accessControl.domain;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;

public class AuthorizationSC extends AuthorizationSC_Base {

    public AuthorizationSC() {
        super();
    }

    public AuthorizationSC(AcademicOperationType operation) {
        this();
        super.setOperation(operation);

    }
}
