package org.example.greetingright.service;

import org.example.greetingright.entity.Role;
import org.example.greetingright.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleSerivceIMPL implements RoleService  {
    private final RoleRepository roleRepository;

    public RoleSerivceIMPL(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findRoleByName(String roleName){
        return roleRepository.findByRoleName(roleName).orElse(null);
    }
}
