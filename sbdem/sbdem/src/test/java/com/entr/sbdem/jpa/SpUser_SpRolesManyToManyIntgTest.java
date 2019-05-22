package com.entr.sbdem.jpa;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.repository.SpRoleRepository;
import com.entr.sbdem.repository.SpUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpUser_SpRolesManyToManyIntgTest {

    @Autowired
    private SpRoleRepository roleRepository;
    @Autowired
    private SpUserRepository userRepository;

    int i;
    int k = 5;//how may users and roles to create
    private List<SpRole> roleList;
    private Set<SpRole> roles = new HashSet<>();
    private Set<SpUser> users = new HashSet<>();

    //Create and persist some data for test.
    @Before
    public void setUp(){
        roleList = new ArrayList<>();
        //Create SpRole's objects and persist them to db
        for(i = 0; i<k; i++){
            SpRole role = new SpRole("SpRole"+i);
            roleRepository.saveAndFlush(role);
            SpRole persistedRole = roleRepository.findByRole("SpRole"+i); // we have separate test for role repo
            roleList.add(persistedRole);
        }
        assertThat(roleList).hasSize(i);
        //Make different number of roles for every user.
        //SpUser0 will have maximum roles. SpUser-Last will have one role
        //SpRole0 will have one assigned user. Last SpRole will have all users.
        for(i=0; i<k; i++){
            SpUser user = new SpUser("SpUser"+i,"password",i+"email1@2.com",new HashSet<SpRole>(roleList));
            userRepository.saveAndFlush(user);
            roleList.remove(0);
        }
    }

    @Test
    public void MtmUserRepoTest() {
        //get user from db
        // first user with number roles(all of them) equal to k
        SpUser persistedUser = userRepository.findByUsername("SpUser0");
        //get set of roles
        roles = persistedUser.getRoles();
        //Last role is number k-1. It must have k user.
        //Get set of users, connected to the role
        users = userRepository.findByRoles_Role("SpRole4");
        //than
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser).isInstanceOf(SpUser.class);
        assertThat(roles).hasSize(k);
        assertThat(users).hasSize(k);
    }

    @Test
    public void whenFindRolesForUserByUsername_getSetOfStringsTest(){
       Set<String> rolesOfSpUser = userRepository.findRolesForUserByUsername("SpUser0");
       assertThat(rolesOfSpUser).hasSize(k);
       assertThat(rolesOfSpUser).contains("SpRole0");
    }

    @Test
    public void whenfindAllByRoles_getSpUserSetTest(){
        SpUser user = userRepository.findByUsername("SpUser0");
        SpRole role = roleRepository.findByRole("SpRole4");
        Set<SpUser> userSet = userRepository.findAllByRoles(role);
        assertThat(userSet).hasSize(5);
        assertThat(userSet).contains(user);
    }

}


