package com.entr.sbdem.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RootSimpleController.class})
@WebMvcTest
public class RootControllerMVCTest {

    private MockMvc mockMvc;

    @Test
    public void shouldReturnDefaultPage() throws Exception{
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RootControllerMVCTest()).defaultRequest(get("/"))
                .alwaysExpect(status().isOk())
                .alwaysExpect(content().string(containsString("Welcome")))
                .build();
    }
}
