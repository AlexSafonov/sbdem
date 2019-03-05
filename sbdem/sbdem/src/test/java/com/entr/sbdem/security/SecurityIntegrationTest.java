package com.entr.sbdem.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenAccessWithoutLogin_redirectToLogin() throws Exception{
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/user")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenAccessWithROLE_USER_403toAdmin() throws Exception{
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/user")).andExpect(status().isOk());
        mvc.perform(get("/admin")).andExpect(status().is4xxClientError());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenAccessWithROLE_ADMIN_fullAccess() throws Exception{
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/user")).andExpect(status().isOk());
        mvc.perform(get("/admin")).andExpect(status().isOk());
    }
}
