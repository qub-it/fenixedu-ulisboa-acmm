package org.fenixedu.accessControl.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;

public class ProfileSC extends ProfileSC_Base {

    @Override
    public void addMember(User member) {
        // TODO Auto-generated method stub

        getGroupSet().forEach(group -> {

        });

        super.addMember(member);
    }

    @Override
    public void removeMember(User member) {
        // TODO Auto-generated method stub

        getGroupSet().forEach(group -> {

        });

        super.removeMember(member);
    }

    @Override
    public void addGroup(PersistentGroup group) {
        // TODO Auto-generated method stub

        getMemberSet().forEach(user -> {

        });

        super.addGroup(group);
    }

    @Override
    public void removeGroup(PersistentGroup group) {
        // TODO Auto-generated method stub

        getMemberSet().forEach(user -> {

        });

        super.removeGroup(group);
    }

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

    public static ProfileSC findByName(String name) {

        final ProfileSC profile = Bennu.getInstance().getProfilescSet().stream()
                .filter(profilesc -> profilesc.getName().equals(name)).findFirst().get();
        return profile;
    }

}