package org.fenixedu.accessControl.domain;

import java.util.Set;

import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule.AcademicAccessTarget;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.DynamicGroup;
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
            final DynamicGroup dynamic = (DynamicGroup) group.toGroup();
            dynamic.mutator().changeGroup(dynamic.underlyingGroup().grant(member));

        });

        super.addMember(member);
    }

    @Override
    public void removeMember(User member) {

        getGroupSet().forEach(group -> {
            final DynamicGroup dynamic = (DynamicGroup) group.toGroup();
            dynamic.mutator().changeGroup(dynamic.underlyingGroup().revoke(member));
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

        super.addGroup(AcademicAuthorizationGroup.get(operation).toPersistentGroup());
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

        super.removeGroup(AcademicAuthorizationGroup.get(operation).toPersistentGroup());

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