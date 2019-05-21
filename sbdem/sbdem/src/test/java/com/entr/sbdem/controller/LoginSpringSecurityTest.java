package com.entr.sbdem.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@Sql("/sql/TESTUSER-postgresql.sql") // Create a user for test. Clean up after test. assign a role using @WithMockUser
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LoginSpringSecurityTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setup(){
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    public void giveValidData_successfullLogIn() throws Exception{
        mvc.perform(formLogin("/perform_login").user("TESTUSER").password("password"))
                .andExpect(status().isFound())
                .andExpect(authenticated().withUsername("TESTUSER"));
    }

    @Test
    public void giveBadData_cantLogIn()throws Exception {
        //Perform login attempt with wrong login and password
        MvcResult result = mvc.perform(formLogin("/perform_login")
                .user("invalid").password("sompassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andDo(print())
                .andReturn();
        //Assert that org.springframework.security.authentication.BadCredentialsException was thrown by Spring Security
        assertTrue(result
                .getRequest()
                .getSession()
                .getAttribute("SPRING_SECURITY_LAST_EXCEPTION")
                .toString()
                .contains("org.springframework.security.authentication.BadCredentialsException: Bad credentials"));

    }
}
