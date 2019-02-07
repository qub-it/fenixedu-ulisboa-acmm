package org.fenixedu.accessControl.domain;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.accessControl.rules.AccessOperation;
import org.fenixedu.bennu.core.domain.Bennu;

public class AuthorizationSC extends AuthorizationSC_Base {

    public AuthorizationSC() {
        super();
    }

    public AuthorizationSC(AcademicOperationType operation) {
        this();
        setBennu(Bennu.getInstance());
        setOperation(operation);

    }

    @Override
    public AccessOperation getOperation() {
        return super.getOperation();
    }

    public static AuthorizationSC get(AcademicOperationType operation) {
        try {
            return Bennu.getInstance().getAuthorizationscSet().stream().filter(auth -> auth.getOperation().equals(operation))
                    .findFirst().get();
        } catch (final Exception e) {
            return new AuthorizationSC(operation);
        }
    }

}
