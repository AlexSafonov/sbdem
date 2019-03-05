package com.entr.sbdem.controller;

import com.entr.sbdem.model.UserRegisterForm;
import com.entr.sbdem.service.SpUserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class RegisterControllerStandaloneSetupTest {

    private MockMvc mvc;

    private SpUserServiceImpl userService;

    @Before
    public void setup() {
        userService = mock(SpUserServiceImpl.class);
        this.mvc = MockMvcBuilders.standaloneSetup(new RegisterController(userService)).build();
    }

    @Test
    public void postMatchingParama_whenPostToForm_returnOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/register")
                .accept(MediaType.TEXT_HTML)
                .param("username", "test")
                .param("password", "password")
                .param("matchPassword","password")
                .param("email", "john@yahoo.com")
        )
                .andExpect(model().attribute("userForm", hasProperty("username", is("test"))))
                .andExpect(model().attribute("userForm", hasProperty("password", is("password"))))
                .andExpect(model().attribute("userForm", hasProperty("matchPassword", is("password"))))
                .andExpect(model().attribute("userForm", hasProperty("email", is("john@yahoo.com"))))
                .andExpect(redirectedUrl("/login"))
                .andExpect(status().isFound())
                .andDo(print());

    }
    //Cheat way. Use .do <- this is what servlet does
    @Test
    public void postMatchingparama_whenPostNewForm_returnException() throws Exception{
        this.mvc.perform(MockMvcRequestBuilders.post("/register.do")
                .accept(MediaType.TEXT_HTML)
                .param("username", "tet")
                .param("password", "word")
                .param("matchPassword","password")
                .param("email", "yahoo.com")
        )
                .andExpect(model().errorCount(4))
                .andExpect(view().name("register"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("register"))
                .andDo(print());
    }
    //Cheat way. Use .do <- this is what servlet does
    @Test
    public void getRegisterForm_returnOk() throws Exception{
        this.mvc.perform(MockMvcRequestBuilders.get("/register.do")
                .accept(MediaType.TEXT_HTML))
                .andExpect(model().attribute("userForm",new UserRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.forwardedUrl("register"));
    }
}
