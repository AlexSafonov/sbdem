package com.entr.sbdem.restController;

import com.entr.sbdem.config.FileUploadPathPropertiesImpl;
import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.UserModifyForm;
import com.entr.sbdem.model.UserRegisterForm;
import com.entr.sbdem.service.SpRoleService;
import com.entr.sbdem.service.SpUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Set;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;





@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/sql/TESTUSER-postgresql.sql") // Create a user for test. Clean up after test. assign a role using @WithMockUser
@Transactional
public class AdminApiControllerInregrationTest {

    @Autowired
    private SpUserService userService;
    @Autowired
    private SpRoleService roleService;

    @Autowired
    private WebApplicationContext wac;

    @Mock
    private FileUploadPathPropertiesImpl uploadPathProperties;

    private MockMvc mvc;


    @Before
    public void setup(){
        this.mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void getExistedUser_returnValidJsonData() throws Exception{
        SpUser user = userService.findUserByUsername("TESTADMIN");
        this.mvc.perform(get("/admin/api/get-user/TESTADMIN").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is(user.getEmail())))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void geAlldUsers_returnValidJsonData() throws Exception{
        Integer dataSize = userService.findAllUsers().size();
        this.mvc.perform(get("/admin/api/get-all-users").with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", hasSize(dataSize)))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void createUserWithValidData_returnUserEntity() throws Exception{
        UserRegisterForm newUser = new UserRegisterForm("NewUser","Password","Password","email@mailmail.com");
        UserRegisterForm a = new UserRegisterForm();
        String newUserAsJson = new ObjectMapper().writeValueAsString(newUser);
        this.mvc.perform(post("/admin/api/create-user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(newUserAsJson))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(newUser.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is(newUser.getEmail())))
                .andDo(print());
        SpUser user = userService.findUserByUsername(newUser.getUsername());
        assertThat(user.getAvatarImgUrl(), is("/images/noImg.jpg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void deleteExistedUser_returnOk() throws Exception{
        Mockito.when(uploadPathProperties.getPathToUploadImages())
                .thenReturn(Paths.get(FileSystemView
                        .getFileSystemView()
                        .getHomeDirectory()
                        .getAbsolutePath())
                        .resolve("sbdem\\upload\\uploadImages"));

        Path tempFilesPath = uploadPathProperties.getPathToUploadImages().resolve("TESTUSER");
        if(!Files.exists(tempFilesPath)) {
            Files.createDirectory(tempFilesPath);
        }
        this.mvc.perform(MockMvcRequestBuilders
                .delete("/admin/api/delete-user/{username}","TESTUSER").with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User was successfully deleted")))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void putRequestExistentUser_returnOk() throws Exception{
        SpUser userBeforeUpdate = userService.findUserByUsername("TESTADMIN");
        String oldpaswrd = userBeforeUpdate.getPassword();
        String oldEmail = userBeforeUpdate.getEmail();
        Set<SpRole> oldRoles = userBeforeUpdate.getRoles();
        //Change password and email, adding ROLE_USER
        String newEmail = "\"newemail@email.com\"";
        String newRoles = "[{\"spRoleId\":2,\"role\":\"ROLE_USER\"},{\"spRoleId\":1,\"role\":\"ROLE_ADMIN\"}]";
        String json = "{\"spUserId\":"+userBeforeUpdate.getSpUserId()+",\"createdAt\":\"2019-01-15 11:17:11\",\"username\":\"TESTADMIN\",\"password\":\"newpassword\",\"email\":"+newEmail+",\"aboutUser\":null,\"avatarImgUrl\":null,\"roles\":"+newRoles+",\"enabled\":true,\"fullname\":null,\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true}";
        this.mvc.perform(put("/admin/api/full-update-user/"+userBeforeUpdate.getSpUserId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User was successfully full updated")));
        SpUser userAfterUpdate = userService.findUserByUsername("TESTADMIN");
        //Same user wih same id
        assertThat(userAfterUpdate.getSpUserId(), is(userBeforeUpdate.getSpUserId()));
        //But password has changed
        assertThat(userAfterUpdate.getPassword(), not(oldpaswrd));
        //email has changed either
        assertThat(userAfterUpdate.getEmail(), is("newemail@email.com"));
        assertThat(userAfterUpdate.getEmail(), not(oldEmail));
        //And ROLE_USER was added
        SpRole userRole = roleService.findByRole("ROLE_USER");
        SpRole adminRole = roleService.findByRole("ROLE_ADMIN");
        assertThat(userAfterUpdate.getRoles(), containsInAnyOrder(userRole,adminRole));
        assertThat(oldRoles, not(containsInAnyOrder(userRole)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "TESTADMIN")
    public void patchExistentUser_returnOk() throws Exception{
        UserModifyForm umf =new UserModifyForm("FullName","valid@email.com","Something about user");
        String umfAsJson = new ObjectMapper().writeValueAsString(umf);
        this.mvc.perform(patch("/admin/api/update-user-info/TESTADMIN")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(umfAsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User was successfully updated")));
        SpUser user = userService.findUserByUsername("TESTADMIN");
        assertThat(user.getEmail(), is(umf.getEmail()));
        assertThat(user.getFullname(), is(umf.getFullName()));
        assertThat(user.getAboutUser(),is(umf.getAboutUser()));
    }
}
