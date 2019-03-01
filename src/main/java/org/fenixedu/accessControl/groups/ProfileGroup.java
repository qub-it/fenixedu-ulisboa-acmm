package org.fenixedu.accessControl.groups;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.accessControl.domain.groups.PersistentProfileGroup;
import org.fenixedu.accessControl.domain.groups.ProfileType;
import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

@GroupOperator(value = "profile")
public class ProfileGroup extends CustomGroup {
    private static final long serialVersionUID = -2479247368353841836L;

    @GroupArgument(value = "")
    private String name;

    ProfileGroup() {
    }

    public ProfileGroup(String name) {
        this();
        this.name = name;
        toPersistentGroup();
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.AccesscontrolResources", "label.profile") + " " + this.name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(String type) {
        toPersistentGroup().setType(ProfileType.get(type));
    }

    public ProfileType getType() {
        return toPersistentGroup().getType();
    }

    @Override
    public PersistentProfileGroup toPersistentGroup() {
        return PersistentProfileGroup.getInstance(this.name)
                .orElseGet(() -> PersistentProfileGroup.set(this.name, Group.nobody().toPersistentGroup()));
    }

    @Override
    public Stream<User> getMembers() {
        return this.toPersistentGroup().getMembers();
    }

    public Stream<User> getMembersWithoutParents() {
        return this.toPersistentGroup().getMembersWithoutParents();
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return this.getMembers();
    }

    @Override
    public boolean isMember(User user) {
        return this.toPersistentGroup().isMember(user);
    }

    public boolean isMemberWithoutParents(User user) {
        return this.toPersistentGroup().isMemberWithoutParents(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return this.isMember(user);
    }

    public Set<PersistentProfileGroup> getParentSet() {
        return this.toPersistentGroup().getParentSet();
    }

    public void addParent(ProfileGroup parent) {
        this.toPersistentGroup().insertParent(parent.toPersistentGroup());
    }

    public void removeParent(ProfileGroup parent) {
        this.toPersistentGroup().removeParent(parent.toPersistentGroup());
    }

    public Set<PersistentProfileGroup> getChildSet() {
        return this.toPersistentGroup().getChildSet();
    }

    @Override
    public Group grant(User user) {
        PersistentProfileGroup.set(this.name, this.persisted().or(user.groupOf()).toPersistentGroup());
        return this;
    }

    @Override
    public Group revoke(User user) {
        PersistentProfileGroup.set(this.name, this.persisted().minus(user.groupOf()).toPersistentGroup());
        return this;
    }

    public void delete() {
        final Optional<PersistentProfileGroup> persistent = PersistentProfileGroup.getInstance(this.name);
        if (persistent.isPresent()) {
            persistent.get().delete();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProfileGroup) {
            return this.name.equals(((ProfileGroup) object).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public Group persisted() {
        final Optional<PersistentProfileGroup> persistent = PersistentProfileGroup.getInstance(this.name);
        if (persistent.isPresent()) {
            return persistent.get().getGroup().toGroup();
        }
        return Group.nobody();
    }
}
