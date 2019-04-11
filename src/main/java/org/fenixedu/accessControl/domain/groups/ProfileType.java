package org.fenixedu.accessControl.domain.groups;

import java.util.Optional;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ProfileType extends ProfileType_Base {

    public ProfileType(String type) {
        super();
        this.setBennu(Bennu.getInstance());
        this.setType(type);
    }

    @Atomic(mode = TxMode.WRITE)
    public static ProfileType get(String type) {
        final Optional<ProfileType> ptype = Bennu.getInstance().getProfileTypeSet().stream()
                .filter(profileType -> profileType.getType().equals(type)).findAny();
        if (ptype.isPresent()) {
            return ptype.get();
        }

        return new ProfileType(type);
    }

}
