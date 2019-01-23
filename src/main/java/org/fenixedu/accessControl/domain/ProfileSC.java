package org.fenixedu.accessControl.domain;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

public class ProfileSC extends ProfileSC_Base {

    public ProfileSC() {
        super();
    }

    public ProfileSC(String name) {
        this();
        if (findByName(name) == null) {
            setBennu(Bennu.getInstance());
            setName(name);
        } else {
            throw new Error("Profile " + name + " already exists!");
        }
    }

    @Override
    public void addMember(User member) {

        getGroupSet().forEach(group -> {

            final Group toGroup = group.toGroup();

            if (toGroup instanceof DynamicGroup) {
                final DynamicGroup dynamic = (DynamicGroup) toGroup;
                dynamic.mutator().changeGroup(dynamic.underlyingGroup().grant(member));
            }

        });

        final Set<AcademicAccessTarget> targets = new HashSet<>();
        getAuthSet().forEach(auth -> {

            new AcademicAccessRule((AcademicOperationType) auth.getOperation(), member.groupOf(), targets,
                    new DateTime("9999-12-31"));

        });

        super.addMember(member);
    }

    public void addMembers(PersistentGroup group) {

        group.getMembers().forEach(member -> {
            addMember(member);
        });

    }

    public void removeMembers(PersistentGroup group) {

        group.getMembers().forEach(member -> {
            removeMember(member);
        });

    }

    @Override
    public void removeMember(User member) {

        getGroupSet().forEach(group -> {
            final Group toGroup = group.toGroup();

            if (toGroup instanceof DynamicGroup) {
                final DynamicGroup dynamic = (DynamicGroup) toGroup;
                dynamic.mutator().changeGroup(dynamic.underlyingGroup().revoke(member));
            }
        });

        AcademicAccessRule.accessRules().forEach(rule -> {
            if (rule.getWhoCanAccess().isMember(member)
                    && getAuthSet().stream().filter(auth -> auth.getOperation().equals(rule.getOperation())).count() > 0) {
                rule.revoke();
            }
        });

        super.removeMember(member);
    }

    @Override
    public void addGroup(PersistentGroup group) {

        final DynamicGroup dynamic = (DynamicGroup) group.toGroup();
        getMemberSet().forEach(user -> {
            dynamic.mutator().changeGroup(dynamic.underlyingGroup().grant(user));
        });

        super.addGroup(group);
    }

    @Override
    public void removeGroup(PersistentGroup group) {
        final DynamicGroup dynamic = (DynamicGroup) group.toGroup();

        getMemberSet().forEach(user -> {
            dynamic.mutator().changeGroup(dynamic.underlyingGroup().revoke(user));
        });

        super.removeGroup(group);
    }

    public void addAuth(AcademicOperationType operation, Set<AcademicAccessTarget> targets, DateTime validity) {

        getMemberSet().forEach(user -> {
            new AcademicAccessRule(operation, user.groupOf(), targets, validity);
        });

        super.addAuth(AuthorizationSC.get(operation));

    }

    public void removeAuth(AcademicOperationType operation) {

        getMemberSet().forEach(user -> {
            try {
                final AcademicAccessRule auth = AcademicAccessRule.accessRules()
                        .filter(a -> a.getWhoCanAccess().equals(user.groupOf())).findFirst().get();
                auth.revoke();
            } catch (final Exception e) {
                // TODO: handle exception
            }
        });

        try {
            super.removeAuth(getAuthSet().stream().filter(auth -> auth.getOperation().equals(operation)).findFirst().get());
        } catch (final Exception e) {
            // TODO: handle exception
        }

    }

    public static ProfileSC findByName(String name) {

        try {
            final ProfileSC profile = Bennu.getInstance().getProfilescSet().stream()
                    .filter(profilesc -> profilesc.getName().equals(name)).findFirst().get();
            return profile;

        } catch (final Exception e) {
            return null;
        }
    }

}