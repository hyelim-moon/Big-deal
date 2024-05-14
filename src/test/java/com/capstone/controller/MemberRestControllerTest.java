package com.capstone.controller;

import com.capstone.dto.member.AddMemberRequest;
import com.capstone.dto.member.LoginMemberRequest;
import com.capstone.dto.member.MemberResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class MemberRestControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test
    public void loginTest() throws Exception {
        LoginMemberRequest request = new LoginMemberRequest("user1","1234");
        ObjectMapper om = new ObjectMapper();
        ResultActions result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grantType", is("Bearer")))
                .andExpect(jsonPath("$.accessToken").exists());
    }
    @Test
    public void loginNotFoundTest() throws Exception {
        LoginMemberRequest request = new LoginMemberRequest("user0", "1234");
        ObjectMapper om = new ObjectMapper();
        ResultActions result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));
        result
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void loginUnauthorizedTest() throws Exception {
        LoginMemberRequest request = new LoginMemberRequest("user1", "1235");
        ObjectMapper om = new ObjectMapper();
        ResultActions result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));
        result
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void loginBadRequestTest() throws Exception {
        LoginMemberRequest request = null;
        ObjectMapper om = new ObjectMapper();
        ResultActions result = null;

        request = new LoginMemberRequest("user1", "");
        result = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request)));
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        request = new LoginMemberRequest("", "1234");
        result = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request)));
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        request = new LoginMemberRequest("user1", "1234");
        result = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.TEXT_PLAIN).content(om.writeValueAsString(request)));
        result
                .andExpect(status().isUnsupportedMediaType())
        //        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        ;
    }
    @Test
    public void userPostTest() throws Exception {
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com");
        ObjectMapper om = new ObjectMapper();
        ResultActions result = mockMvc.perform(post("/api/v1/member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));
        String response = result.andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").toString();
        System.out.println("[my]username: " + username);
        result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new LoginMemberRequest(username.replace("\"", ""), "1234"))));
        result
                .andExpect(status().isOk());
    }
}
