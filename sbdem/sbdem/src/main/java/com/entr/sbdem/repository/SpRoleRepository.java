package com.entr.sbdem.repository;

import com.entr.sbdem.entity.SpRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpRoleRepository extends JpaRepository<SpRole,Long> {
    SpRole findByRole(String role);
}
