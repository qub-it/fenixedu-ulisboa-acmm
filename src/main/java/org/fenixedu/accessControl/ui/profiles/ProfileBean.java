package org.fenixedu.accessControl.ui.profiles;

import java.util.Set;

import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;

public class ProfileBean {

    String name;
    String cod;
    String type;
    Set<AcademicOperationType> auths;
    Set<String> parents;
    Set<String> menus;

    public ProfileBean(String name, String cod, String type, Set<AcademicOperationType> auths, Set<String> parents,
            Set<String> menus) {
        this.name = name;
        this.cod = cod;
        this.type = type;
        this.auths = auths;
        this.parents = parents;
        this.menus = menus;
    }

    public Set<AcademicOperationType> getAuths() {
        return auths;
    }

    public Set<String> getParents() {
        return parents;
    }

    public Set<String> getMenus() {
        return menus;
    }

    public String getName() {
        return name;
    }

    public String getCod() {
        return cod;
    }

    public String getType() {
        return type;
    }

}