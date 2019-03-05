package com.entr.sbdem.jpa;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.repository.SpUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class SpUserRepositoryTest {

    @Autowired
    private SpUserRepository spUserRepository;

    @Test
    public void SpUserCorrectMappingTest(){
        //given
        SpUser testbot1 = new SpUser("testbotfromJPA",
                "password",
                "bot@botmail.com");
        SpUser testst = spUserRepository.saveAndFlush(testbot1);
        assertThat(testst).isNotNull();

        //when
        SpUser found = spUserRepository.findByUsername(testbot1.getUsername());
        //then
        Timestamp rightNow = new Timestamp(System.currentTimeMillis());
        assertThat(rightNow).isAfter(found.getCreatedAt());
        assertThat(found.getUsername()).isEqualTo(testbot1.getUsername());
        //Password is not encrypted here. Will do it in service class.
        assertThat(found.getPassword()).isEqualTo(testbot1.getPassword());
        //asserting the default values of SpUser are set
        assertThat(found.isAccountNonExpired()).isNotNull();
        assertThat(found.isAccountNonExpired()).isEqualTo(true);

        assertThat(found.isAccountNonLocked()).isNotNull();
        assertThat(found.isAccountNonLocked()).isEqualTo(true);

        assertThat(found.isCredentialsNonExpired()).isNotNull();
        assertThat(found.isCredentialsNonExpired()).isEqualTo(true);

        assertThat(found.isEnabled()).isNotNull();
        assertThat(found.isEnabled()).isEqualTo(true);
    }

    @Test(expected = NullPointerException.class)
    public void whenPassNullTofindMethod_NullPointerException(){
        SpUser user = null;
        spUserRepository.findByEmail(user.getEmail());
        spUserRepository.findByUsername(user.getUsername());
    }
    @Test
    public void deleteUserTest(){
        SpUser testbot1 = new SpUser("testbotfromJPA",
                "password",
                "bot@botmail.com");
        SpUser testst = spUserRepository.saveAndFlush(testbot1);
        spUserRepository.deleteSpUserByUsername("testbotfromJPA");
    }
}
