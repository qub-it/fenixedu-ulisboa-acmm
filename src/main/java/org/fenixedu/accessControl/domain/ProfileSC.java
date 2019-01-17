package org.fenixedu.accessControl.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.DynamicGroup;

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

    public static ProfileSC findByName(String name) {

        final ProfileSC profile = Bennu.getInstance().getProfilescSet().stream()
                .filter(profilesc -> profilesc.getName().equals(name)).findFirst().get();
        return profile;
    }

}