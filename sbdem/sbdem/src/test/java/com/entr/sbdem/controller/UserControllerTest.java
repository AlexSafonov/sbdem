package com.entr.sbdem.controller;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.StorageException;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.UserModifyForm;
import com.entr.sbdem.service.AWSs3StorageService;
import com.entr.sbdem.service.FileSystemStorageService;
import com.entr.sbdem.service.SpUserService;
import com.entr.sbdem.service.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql("/sql/TESTUSER-postgresql.sql") // Create a user for test. Clean up after test. assign a role using @WithMockUser
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private SpUserService userService;
    @MockBean
    private AWSs3StorageService storageService;

    private MockMvc mvc;

    @Before
    public void setUp(){
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")//User realy exist in DB. If not we'll get NullPointer
    public void getCompletedForm_returnOk() throws Exception{
        mvc.perform(get("/user/modify-user").with(csrf()))
                .andExpect(model().attribute("userModifyForm",any(UserModifyForm.class)))
                .andExpect(status().isOk())
                .andExpect(view().name("modify-user"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void submitInvalidData_returnErrorAndModelBack() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post("/user/modify-user")
                .with(csrf())
                .accept(MediaType.TEXT_HTML)
                .param("fullName","Richard TestBot The First") // too long max = 20 char
                .param("email","ohni") // not a email
                .param("aboutUser","Story of life in 255 letters")
        )
                .andExpect(model().attribute("userModifyForm",any(UserModifyForm.class)))
                .andExpect(model().attribute("userModifyForm", hasProperty("fullName", is("Richard TestBot The First"))))
                .andExpect(model().attribute("userModifyForm", hasProperty("email", is("ohni"))))
                .andExpect(model().attribute("userModifyForm", hasProperty("aboutUser", is("Story of life in 255 letters"))))
                .andExpect(model().errorCount(2))
                .andExpect(view().name("modify-user"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void submitValidParam_returnRedirectBackToPage() throws Exception{
        mvc.perform(
                MockMvcRequestBuilders.post("/user/modify-user")
                        .with(csrf())
                        .accept(MediaType.TEXT_HTML)
                        .param("fullName","Richard The TestBot")
                        .param("email","testest@osos.com")
                        .param("aboutUser","Story of life in 255 letters")
        )
                .andExpect(flash().attribute("rdrAtr","Your profile was successfully updated"))
                .andExpect(model().errorCount(0))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/modify-user"))
                .andDo(print());
    }


    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void uploadValidImage_returnOk() throws Exception{
        doNothing().when(storageService).storeUserAvatarImage(Mockito.any(),Mockito.any());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mlp",
                "image.png",
                "image/png",
                "<<image.png>>".getBytes());

        this.mvc.perform(
                MockMvcRequestBuilders.multipart("/user/modify-user-img")
                        .file(mockMultipartFile)
                        .with(csrf())
        )
                .andExpect(flash().attribute("imageRdrAtr", "Your image Avatar was successfully updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/modify-user"))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void uploadNonImageFile_returnStorageException() throws Exception{
        String filename = "image.txt";
        doThrow(new StorageException("Uploaded file is not a valid image. Only JPG, PNG and GIF files are allowed. Filename: " + filename)).when(storageService).storeUserAvatarImage(Mockito.any(),Mockito.any());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mlp",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                "<<image.txt>>".getBytes());

        MvcResult result = this.mvc.perform(
                MockMvcRequestBuilders.multipart("/user/modify-user-img")
                        .file(mockMultipartFile)
                        .with(csrf()))
               .andExpect(model().attribute("msg",equalTo("Uploaded file is not a valid image. Only JPG, PNG and GIF files are allowed. Filename: " + filename)))
               .andExpect(status().is5xxServerError())
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass() , typeCompatibleWith(StorageException.class) );
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void userInfoPageByUsername_returnUserHomePage() throws Exception{
        SpUser user = userService.findUserByUsername("TESTUSER");
        this.mvc.perform(get("/user/TESTUSER"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user.getEmail())))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void userInfoPageByAnotherUsername_returnUserInfoPage() throws Exception{
        this.mvc.perform(get("/user/TESTADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"))
                .andExpect(content().string(containsString("TESTADMIN")))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER", username = "TESTUSER")
    public void userInfoPageByInvalidUsername_returnException() throws Exception{
        MvcResult result = this.mvc.perform(get("/user/INVALIDTESTUSER"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
        assertThat(result.getResolvedException().getClass(),typeCompatibleWith(UserNotFoundException.class));
    }

}
