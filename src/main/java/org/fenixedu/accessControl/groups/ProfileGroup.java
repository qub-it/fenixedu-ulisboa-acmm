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
    private String cod;

    ProfileGroup() {
    }

    public ProfileGroup(String cod) {
        this();
        this.cod = cod;
        toPersistentGroup();
    }

    public void setName(String name) {
        toPersistentGroup().setName(name);
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString("resources.AccesscontrolResources", "label.profile") + " " + toPersistentGroup().getName();
    }

    public String getCod() {
        return this.cod;
    }

    public String getName() {
        return toPersistentGroup().getName();
    }

    public void setType(String type) {
        toPersistentGroup().setType(ProfileType.get(type));
    }

    public ProfileType getType() {
        return toPersistentGroup().getType();
    }

    @Override
    public PersistentProfileGroup toPersistentGroup() {
        return PersistentProfileGroup.getInstance(this.cod)
                .orElseGet(() -> PersistentProfileGroup.set(this.cod, Group.nobody().toPersistentGroup()));
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
        PersistentProfileGroup.set(this.cod, this.persisted().or(user.groupOf()).toPersistentGroup());
        return this;
    }

    @Override
    public Group revoke(User user) {
        PersistentProfileGroup.set(this.cod, this.persisted().minus(user.groupOf()).toPersistentGroup());
        return this;
    }

    public void delete() {
        final Optional<PersistentProfileGroup> persistent = PersistentProfileGroup.getInstance(this.cod);
        if (persistent.isPresent()) {
            persistent.get().delete();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ProfileGroup) {
            return this.cod.equals(((ProfileGroup) object).cod);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.cod.hashCode();
    }

    public Group persisted() {
        final Optional<PersistentProfileGroup> persistent = PersistentProfileGroup.getInstance(this.cod);
        if (persistent.isPresent()) {
            return persistent.get().getGroup().toGroup();
        }
        return Group.nobody();
    }
}
