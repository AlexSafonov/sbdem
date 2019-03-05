package com.entr.sbdem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.IOException;

@Configuration
public class BeanConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

}
