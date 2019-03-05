package com.entr.sbdem.repository;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface SpUserRepository extends JpaRepository<SpUser,Long> {
    SpUser findByUsername(String username);
    SpUser findBySpUserId(Long id);
    SpUser findByEmail(String enail);
    Set<SpUser> findByRoles_Role(String role);
    Set<SpUser> findAllByRoles(SpRole role);
    List<SpUser> findAll();
    //Same as get method for every role from roles Set in SpUser obj
    @Query ("select r.role from SpRole r " +
            "inner join r.users u " +
            "where u.username = :name")
    Set<String> findRolesForUserByUsername(@Param("name") String username);
    @Transactional
    @Modifying
    void deleteSpUserByUsername(String username);





}
