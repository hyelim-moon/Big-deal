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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void uuidNegative() throws Exception {
        String url = "/api/v1/franchise/1L";
        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isNotFound());
    }
    @Test
    public void center() throws Exception {
        String url = "/api/v1/franchise?la=37.64373254&lo=127.1415573";

        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

    }
    @Test
    public void centerNegative() throws Exception {
        String url = "/api/v1/franchise?la=37.64373254";

        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());

        url = "/api/v1/franchise?la=&lo=";
        final ResultActions emptyResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        emptyResult.andExpect(status().isBadRequest());
        url = "/api/v1/franchise?la=hi&lo=hello";
        final ResultActions charResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        charResult.andExpect(status().isBadRequest());
        url = "/api/v1/franchise";
        final ResultActions invalidateUrlResult = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        invalidateUrlResult.andExpect(status().isBadRequest());
    }

}
