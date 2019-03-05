package com.entr.sbdem.service;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.exception.RoleNotFoundException;
import com.entr.sbdem.repository.SpRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpRoleServiceImpl implements SpRoleService {

    private final SpRoleRepository roleRepository;

    public SpRoleServiceImpl(final SpRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public SpRole findByRole(String role) {
        SpRole spRole = roleRepository.findByRole(role);
        if(spRole == null){throw new RoleNotFoundException(role + " does not exists");}
        return spRole;
    }
}
