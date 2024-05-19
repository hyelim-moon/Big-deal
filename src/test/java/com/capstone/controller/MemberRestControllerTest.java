package com.capstone.controller;

import com.capstone.config.WebSecurityConfig;
import com.capstone.dto.member.*;
import com.capstone.provider.PinNumberProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class MemberRestControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    private final ObjectMapper om = new ObjectMapper();
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private PinNumberProvider pinNumberProvider;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        doReturn("123456").when(pinNumberProvider).createCode();
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
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));

        ResultActions result = mockMvc.perform(post("/api/v1/member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));

        result.andExpect(status().isCreated());
        String response = result.andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").toString();
        //System.out.println("[my]username: " + username);
        result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new LoginMemberRequest(username.replace("\"", ""), "1234"))));
        result
                .andExpect(status().isOk());
    }
    @Test
    public void memberPostBadRequestTest() throws Exception {
        AddMemberRequest request1, request2, request3;
        SendToEmailMemberRequest emailRequest1, emailRequest2, emailRequest3;
        ResultActions result1, result2, result3;
        request1 = new AddMemberRequest("", "1234", "user@email.com", "123456");
        emailRequest1 = new SendToEmailMemberRequest(request1.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest1)));
        request2 = new AddMemberRequest("id1", "", "user@email.com", "123456");
        emailRequest2 = new SendToEmailMemberRequest(request2.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest2)));
        request3 = new AddMemberRequest("id1", "1234", "", "123456");
        emailRequest3 = new SendToEmailMemberRequest(request3.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest3)));

        result1 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request1)));
        result2 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request2)));
        result3 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request3)));

        result1.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result2.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result3.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberPostForbiddenTest() throws Exception {
        AddMemberRequest firstRequest, request1, request2;
        SendToEmailMemberRequest emailRequestFirst, emailRequest1, emailRequest2;
        ResultActions result1, result2;
        firstRequest = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        emailRequestFirst = new SendToEmailMemberRequest(firstRequest.getEmail());
        request1 = new AddMemberRequest("id1", "5678", "member@email.com", "123456");
        emailRequest1 = new SendToEmailMemberRequest(request1.getEmail());
        request2 = new AddMemberRequest("id2", "90","user@email.com", "123456");
        emailRequest2 = new SendToEmailMemberRequest(request2.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequestFirst)));
        mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(firstRequest)));
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest1)));
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest2)));

        result1 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request1)));
        result2 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request2)));

        result1.andExpect(status().isForbidden()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result2.andExpect(status().isForbidden()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberGetUuidTest() throws Exception {
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request))).andReturn().getResponse().getContentAsString();
        String uuid = om.readTree(response).get("uuid").asText();//System.out.println("[my] uuid = " + uuid);

        result = mockMvc.perform(get("/api/v1/member/" + uuid).contentType(MediaType.APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("id1"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }
    @Test
    public void memberGetUuidNotFoundTest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/member/1L").contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberGetTest() throws Exception {
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request))).andReturn().getResponse().getContentAsString();
        //String uuid = om.readTree(response).get("uuid").asText();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(response).get("grantType").asText();
        String accessToken = om.readTree(response).get("accessToken").asText();

        result = mockMvc.perform(get("/api/v1/member").contentType(MediaType.APPLICATION_JSON).header("Authorization", grantType + " " + accessToken));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    @Test
    public void memberGetUnauthorizedTest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/v1/member").contentType(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberUpdateTest() throws Exception {
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        UpdateMemberRequest updateRequest = new UpdateMemberRequest("id2","5678", "member@email.com");
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request))).andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(response).get("grantType").asText();
        String accessToken = om.readTree(response).get("accessToken").asText();

        result = mockMvc.perform(put("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRequest)).header("Authorization", grantType + " " + accessToken));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updateRequest.getUsername()))
                .andExpect(jsonPath("$.email").value(updateRequest.getEmail()));
    }
    @Test
    public void memberUpdateUnauthorizedTest() throws Exception {
        ResultActions result = mockMvc.perform(put("/api/v1/member").contentType(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberWithdrawalTest() throws Exception {
        AddMemberRequest addRequest = new AddMemberRequest("id1", "1234", "user@email.com", "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(addRequest.getEmail());
        ResultActions result1, result2;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRequest))).andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(response).get("grantType").asText();
        String accessToken = om.readTree(response).get("accessToken").asText();

        result1 = mockMvc.perform(delete("/api/v1/member").contentType(MediaType.APPLICATION_JSON).header("Authorization", grantType + " " + accessToken));
        result2 = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234"))));

        result1.andExpect(status().isOk())
                .andExpect(content().string("true"));
        result2
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void memberWithdrawalUnauthorizedTest() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/v1/member").contentType(MediaType.APPLICATION_JSON));
        result
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void sendCodeToEmailTest() throws Exception {
        SendToEmailMemberRequest requestEmail = new SendToEmailMemberRequest("choda1510@kyungmin.ac.kr");

        ResultActions result1 = mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(requestEmail)));
        result1
                .andExpect(status().isOk());
    }
    @Test
    public void sendCodeToEmailNegativeTest() throws Exception {
        SendToEmailMemberRequest requestEmail = new SendToEmailMemberRequest("choda1510@kyungmin.ac.kr");
        AddMemberRequest addMemberRequest = new AddMemberRequest("id1", "password1", "id1@email.com", "123457");

        ResultActions result1 = mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(requestEmail)));
        ResultActions result2 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addMemberRequest)));

        result1
                .andExpect(status().isOk());
        result2
                .andExpect(status().isUnauthorized());
    }
}
