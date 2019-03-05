package com.entr.sbdem.service;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.repository.SpUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SpUserServiceTest {

    private SpUserServiceImpl userService;

    @MockBean
    private SpUserRepository mockedUserRepository;

    @MockBean
    private SpRoleServiceImpl mockedRoleService;

    @MockBean
    private BCryptPasswordEncoder mockedEncoder;

    private SpUser testUser;
    private SpRole testRole;

    Set<SpRole> spRolesSet;
    Set<String> roles;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        //Build up some mock data. Is there a reason to use mock?
        testRole = new SpRole("ROLE_USER");
        spRolesSet = new HashSet<>();
        spRolesSet.add(testRole);
        roles = new HashSet<>();
        roles.add("USER");
        testUser = new SpUser("TestBot","password","botmail@mail", spRolesSet);
        userService = new SpUserServiceImpl(mockedUserRepository,mockedRoleService,mockedEncoder);
}

    @Test
    public void whenFindByUsername_returnUser(){
        //given
        when(mockedUserRepository.findByUsername("TestBot")).thenReturn(testUser);
        //when
        SpUser found = userService.findUserByUsername("TestBot");
        //then
        //No reason to test all of SpUser fields.
        assertThat(found.getUsername()).isEqualTo(testUser.getUsername());
      //  assertThat(found.getCreatedAt().isEqual(testUser.getCreatedAt()));
    }

    @Test
    public void whenFindByEmail_returnUser(){
        //given
        when(mockedUserRepository.findByEmail("botmail@mail")).thenReturn(testUser);
        //when
        SpUser found = userService.findUserByEmail("botmail@mail");
        //then
        assertThat(found.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(found.getCreatedAt().equals(testUser.getCreatedAt()));

    }

    @Test
    public void whenFindByID_returnUser(){
        //given
        testUser.setSpUserId(777L);
        when(mockedUserRepository.findBySpUserId(777L)).thenReturn(testUser);
        //when
        SpUser found = userService.findUserById(777L);
        //then
        assertThat(found.getSpUserId()).isEqualTo(testUser.getSpUserId());
        assertThat(found.isAccountNonExpired()).isEqualTo(testUser.isAccountNonExpired());
    }

    @Test
    public void whenGetUserRoles_returnSetOfRoles(){
        //given. Look in setUp();
        when(mockedUserRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);
        when(mockedUserRepository.findRolesForUserByUsername(Mockito.anyString())).thenReturn(roles);
        //when
        Set<String> usersRoles = userService.getUserRoles("TestBot");
        //then
        assertThat(usersRoles).containsOnly("USER");
    }

    @Test
    public void whenUserAlreadyExist_returnTRUE(){
        //given
        when(mockedUserRepository.findByUsername("TestBot")).thenReturn(testUser);
        when(mockedUserRepository.findByEmail("botmail@mail")).thenReturn(testUser);
        //when
        boolean byUsername = userService.userAlreadyExist(testUser.getUsername(), testUser.getEmail());
        boolean bySpUerObject = userService.userAlreadyExist(testUser);
        //then
        assertThat(bySpUerObject).isEqualTo(true);
        assertThat(byUsername).isEqualTo(true);
    }
    @Test
    public void whenUserAlreadyExist_returnFALSE(){
        ///given
        when(mockedUserRepository.findByUsername("NOT_A_TestBot")).thenReturn(null);
        when(mockedUserRepository.findByEmail("Not_A_TestBot_Mail")).thenReturn(null);
        //when
        boolean byUsername = userService.userAlreadyExist(testUser.getUsername(), testUser.getEmail());
        boolean bySpUerObject = userService.userAlreadyExist(testUser);
        //then
        assertThat(bySpUerObject).isEqualTo(false);
        assertThat(byUsername).isEqualTo(false);
    }
    @Test
    public void whenSaveUserWithROLE_USER_returnSpUserWithROLE_USER(){
       // SpUser realUser = new SpUser("RealUser","realpassword","realEmail",spRolesSet);

        when(mockedRoleService.findByRole(Mockito.any())).thenReturn(testRole);
        when(mockedEncoder.encode("password")).thenReturn("cryptopassword");

        when(mockedUserRepository.saveAndFlush(Mockito.any())).thenReturn(testUser);

        SpUser savedByObj = userService.saveUserWithROLE_USER(testUser);
        SpUser savedByFields = userService.saveUserWithROLE_USER("TestBot","password","botmail@mail");
        //then
        assertThat(testUser).isNotNull();
        assertThat(savedByObj).isNotNull();
        assertThat(savedByFields).isNotNull();
        assertThat(savedByFields.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(savedByFields.getRoles()).containsOnly(testRole);
    }
    @Test
    public void deleteExistedUser_returnNothing(){
        when(mockedUserRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);
        userService.deleteUserByUsername("TestBot");
    }
    @Test(expected = UserNotFoundException.class)
    public void deleteNonExistedUser_thowException(){
        userService.deleteUserByUsername("NotFoundSomeRandomNumberAndStrings123");
    }
}
