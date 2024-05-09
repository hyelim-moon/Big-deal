package com.capstone.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class FranchiseRestControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    public void beforeMock() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test
    public void between() throws Exception {
        String url = "/api/franchise?fromLa=37.64373254&toLa=37.64373254&fromLo=127.1415573&toLo=127.1415573";

        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].latitude").value(new BigDecimal("37.64373254").setScale(30)))
                .andExpect(jsonPath("$[0].longitude").value(new BigDecimal("127.1415573").setScale(30)));

    }
    @Test
    public void betweenNegative() throws Exception {
        String url = "/api/franchise?fromLa=37.64373254&toLa=37.64373254";

        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());

        url = "/api/franchise?fromLa=three&toLa=two&fromLo=one&toLo=fire";
        final ResultActions charResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        charResult.andExpect(status().is4xxClientError());
    }

}
