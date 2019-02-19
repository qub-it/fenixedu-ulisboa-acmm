package org.fenixedu.accessControl.domain.groups;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.accessControl.groups.ProfileGroup;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.dml.runtime.Relation;

public class PersistentProfileGroup extends PersistentProfileGroup_Base {

    protected PersistentProfileGroup(String name, PersistentGroup group) {
        this.setBennu(Bennu.getInstance());
        this.setName(name);
        this.setGroup(group);
        this.setCreator(Authenticate.getUser());
        this.setCreated(DateTime.now());
    }

    @Override
    public Group toGroup() {
        return new ProfileGroup(this.getName());
    }

    @Override
    public Stream<User> getMembers() {
        final Set<User> members = new HashSet<>();

        members.addAll(this.getGroup().getMembers().collect(Collectors.toSet()));

        getFatherSet().forEach(father -> {
            members.addAll(father.getMembers().collect(Collectors.toSet()));
        });

        return members.stream();
    }

    public Stream<User> getMembersWithoutFathers() {
        return this.getGroup().getMembers();
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return this.getMembers();
    }

    @Override
    public boolean isMember(User user) {

        final Optional<PersistentProfileGroup> fathers =
                getFatherSet().stream().filter(father -> father.isMember(user)).findAny();

        if (fathers.isPresent()) {
            return true;
        } else {
            return this.getGroup().isMember(user);
        }
    }

    public boolean isMemberWithoutFathers(User user) {
        return this.getGroup().isMember(user);
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return this.isMember(user);
    }

    public PersistentProfileGroup changeGroup(PersistentGroup overridingGroup) {
        if (!overridingGroup.equals(this.getGroup())) {
            this.setGroup(overridingGroup);
        }
        return this;
    }

    @Atomic(mode = TxMode.WRITE)
    public static PersistentProfileGroup set(String name, PersistentGroup overridingGroup) {
        final Optional<PersistentProfileGroup> persistent = PersistentProfileGroup.getInstance(name);
        if (persistent.isPresent()) {
            return persistent.get().changeGroup(overridingGroup);
        }
        return new PersistentProfileGroup(name, overridingGroup);
    }

    @Override
    protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.singleton(PersistentProfileGroup.getRelationPersistentProfileGroups());
    }

    public static Optional<PersistentProfileGroup> getInstance(String name) {
        return Bennu.getInstance().getProfileGroupSet().stream().filter(group -> group.getName().equals(name)).findAny();
    }

    public void delete() {
        this.setBennu(null);
        this.setName(null);
        this.setGroup(Group.nobody().toPersistentGroup());
    }
}