package com.entr.sbdem.service;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.repository.SpRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SpRoleServiceTest {

    @Mock
    private SpRoleRepository mockedRoleRepo;

    private SpRoleService roleService;
    private SpRole testSpRole;

    @Before
    public void setUp(){
        roleService = new SpRoleServiceImpl(mockedRoleRepo);
        testSpRole = new SpRole("TEST_ROLE");
    }

    @Test
    public void whenFindByRole_returnSpRole(){
        when(mockedRoleRepo.findByRole("TEST_ROLE")).thenReturn(testSpRole);
        SpRole foundRole = roleService.findByRole("TEST_ROLE");
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getRole()).isEqualTo(testSpRole.getRole());
    }


}
