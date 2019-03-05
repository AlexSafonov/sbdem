package com.entr.sbdem.restController;

import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.UserRegisterForm;
import com.entr.sbdem.service.SpUserService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/sql/TESTUSER-postgresql.sql") // Create a user for test. Clean up after test. assign a role using @WithMockUser
@Transactional
public class AdminApiControllerAdviceIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setup(){
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryTodDeleteNonExistentUser_returnNotFound() throws Exception{
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders
                .delete("/admin/api/delete-user/NonExistent","TESTUSER").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Handling UserNotFoundException")))
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(UserNotFoundException.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryToGetNonExistentUser_returnNotFound() throws Exception{
        MvcResult result = this.mvc.perform(get("/admin/api/get-user/NonExisten").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Handling UserNotFoundException")))
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(UserNotFoundException.class));
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryToPerformGetRequestOnDeletePath_got405() throws Exception{
        MvcResult result = this.mvc.perform(get("/admin/api/delete-user/NonExistent)").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(containsString("Supported methods are")))
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(HttpRequestMethodNotSupportedException.class));
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryToPerformRequestWithBadMediaType_returnUnsupportedMediaType() throws Exception{
        MvcResult result = this.mvc.perform(post("/admin/api/create-user")
                .with(csrf()).contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isUnsupportedMediaType())
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(HttpMediaTypeNotSupportedException.class));
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryToPerformRequestWithBadData_returnHttpMessageNotReadableException() throws Exception{
        String invalidData = "UserRegisterForm(username=NewUser, password=Password, matchPassword=Password, email=email@mailmail.com)";
        MvcResult result = this.mvc.perform(post("/admin/api/create-user")
                .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(invalidData))
                .andExpect(content().string(containsString("Malformed JSON request")))
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(HttpMessageNotReadableException.class));
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void tryToPerformRequestWithNotValidData_returnMethodArgumentNotValidException() throws Exception {
        String invalidData = "{\"username\": \"ser\", \"password\": \"Password\", \"matchPassword\": \"Password\", \"email\": \"email\"}";
        MvcResult result = this.mvc.perform(post("/admin/api/create-user")
                .with(csrf()).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(invalidData))
                .andExpect(content().string(containsString("Validation error")))
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass(), typeCompatibleWith(MethodArgumentNotValidException.class));
    }
}


