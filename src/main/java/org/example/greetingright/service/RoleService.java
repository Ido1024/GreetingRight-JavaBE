package org.example.greetingright.service;

import org.example.greetingright.entity.Role;

public interface RoleService {
    Role findRoleByName(String roleName);
}
