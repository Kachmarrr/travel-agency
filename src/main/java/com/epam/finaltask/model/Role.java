package com.epam.finaltask.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum Role {

    ADMIN(EnumSet.of(
            Permission.ADMIN_CREATE,
            Permission.ADMIN_READ,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE,
            Permission.MANAGER_UPDATE,
            Permission.USER_CREATE,
            Permission.USER_READ,
            Permission.USER_UPDATE,
            Permission.USER_DELETE
            )),

    MANAGER(EnumSet.of(
            Permission.MANAGER_UPDATE,
            Permission.USER_READ,
            Permission.ADMIN_UPDATE
    )),

    USER(EnumSet.of(
            Permission.USER_READ
    ));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }



}
