package com.entr.sbdem.jpa;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.repository.SpRoleRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class SpRoleRepositoryTest {

    @Autowired
    private SpRoleRepository repo;

    //To be sure that auto generation works with id field
    //and repo works fine
    @Test
    public void spRoleRepositoryTest(){

        SpRole role = new SpRole("OP");
        repo.saveAndFlush(role);

        SpRole op = repo.findByRole("OP");

        assertNotNull( op.getSpRoleId());
        assertEquals(role.getRole(),op.getRole());
    }
}
