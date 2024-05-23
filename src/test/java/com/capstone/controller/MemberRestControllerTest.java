package com.capstone.controller;

import com.capstone.dto.member.*;
import com.capstone.provider.PinNumberUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.catalina.security.SecurityConfig;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doReturn;
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
    private PinNumberUtility pinNumberUtility;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        doReturn("123456").when(pinNumberUtility).createCode();
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
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(request.getEmail(), "123456");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(singUpResponse).get("grantType").asText();
        String token = om.readTree(singUpResponse).get("accessToken").asText();

        ResultActions result = mockMvc.perform(post("/api/v1/member")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request))
                .header("Authorization", grantType + " " + token));

        result.andExpect(status().isCreated());
        String response = result.andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").asText();
        result = mockMvc.perform(post("/api/v1/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new LoginMemberRequest(username, "1234"))));
        result
                .andExpect(status().isOk());
    }
    @Test
    public void memberPostBadRequestTest() throws Exception {
        AddMemberRequest request1, request2, request3;
        SendToEmailMemberRequest emailRequest1, emailRequest2, emailRequest3;
        VerifiedMemberRequest verifiedMemberRequest1, verifiedMemberRequest2, verifiedMemberRequest3;
        String singUpResponse, grantType, accessToken1, accessToken2, accessToken3;
        ResultActions result1, result2, result3;
        request1 = new AddMemberRequest("", "1234", "user@email.com");
        emailRequest1 = new SendToEmailMemberRequest(request1.getEmail());
        verifiedMemberRequest1 = new VerifiedMemberRequest(request1.getEmail(), "123456");
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest1)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest1))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(singUpResponse).get("grantType").asText();
        accessToken1 = om.readTree(singUpResponse).get("accessToken").asText();
        request2 = new AddMemberRequest("id1", "", "user@email.com");
        emailRequest2 = new SendToEmailMemberRequest(request2.getEmail());
        verifiedMemberRequest2 = new VerifiedMemberRequest(request2.getEmail(), "123456");
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest2)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest2))).andReturn().getResponse().getContentAsString();
        accessToken2 = om.readTree(singUpResponse).get("accessToken").asText();
        request3 = new AddMemberRequest("id1", "1234", "");
        emailRequest3 = new SendToEmailMemberRequest(request3.getEmail());
        verifiedMemberRequest3 = new VerifiedMemberRequest(request3.getEmail(), "123456");
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest3)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest3))).andReturn().getResponse().getContentAsString();
        accessToken3 = om.readTree(singUpResponse).get("accessToken").asText();

        result1 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request1)).header("Authorization", grantType + " " + accessToken1));
        result2 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request2)).header("Authorization", grantType + " " + accessToken2));
        result3 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request3)).header("Authorization", grantType + " " + accessToken3));

        result1.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result2.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result3.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberPostForbiddenTest() throws Exception {
        AddMemberRequest firstRequest, request1, request2;
        SendToEmailMemberRequest emailRequestFirst, emailRequest1, emailRequest2;
        VerifiedMemberRequest verifiedMemberRequestFirst, verifiedMemberRequest1, verifiedMemberRequest2;
        String singUpResponse, grantType, accessTokenFirst, accessToken1, accessToken2;
        ResultActions result1, result2;
        firstRequest = new AddMemberRequest("id1", "1234", "user@email.com");
        emailRequestFirst = new SendToEmailMemberRequest(firstRequest.getEmail());
        verifiedMemberRequestFirst = new VerifiedMemberRequest(firstRequest.getEmail(), "123456");
        request1 = new AddMemberRequest("id1", "5678", "member@email.com");
        emailRequest1 = new SendToEmailMemberRequest(request1.getEmail());
        verifiedMemberRequest1 = new VerifiedMemberRequest(request1.getEmail(), "123456");
        request2 = new AddMemberRequest("id2", "90","user@email.com");
        emailRequest2 = new SendToEmailMemberRequest(request2.getEmail());
        verifiedMemberRequest2 = new VerifiedMemberRequest(request2.getEmail(), "123456");
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequestFirst)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequestFirst))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(singUpResponse).get("grantType").asText();
        accessTokenFirst = om.readTree(singUpResponse).get("accessToken").asText();
        mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(firstRequest)).header("Authorization", grantType + " " + accessTokenFirst));
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest1)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest1))).andReturn().getResponse().getContentAsString();
        accessToken1 = om.readTree(singUpResponse).get("accessToken").asText();
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest2)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest2))).andReturn().getResponse().getContentAsString();
        accessToken2 = om.readTree(singUpResponse).get("accessToken").asText();

        result1 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request1)).header("Authorization", grantType + " " + accessToken1));
        result2 = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request2)).header("Authorization", grantType + " " + accessToken2));

        result1.andExpect(status().isForbidden()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result2.andExpect(status().isForbidden()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    public void memberGetUuidTest() throws Exception {
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(request.getEmail(), "123456");
        String singUpResponse, grantType, accessToken;
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(singUpResponse).get("grantType").asText();
        accessToken = om.readTree(singUpResponse).get("accessToken").asText();
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request)).header("Authorization", grantType + " " + accessToken)).andReturn().getResponse().getContentAsString();
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
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(request.getEmail(), "123456");
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(singUpResponse).get("grantType").asText();
        String accessToken = om.readTree(singUpResponse).get("accessToken").asText();
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request)).header("Authorization", grantType + " " + accessToken)).andReturn().getResponse().getContentAsString();
        //String uuid = om.readTree(response).get("uuid").asText();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(response).get("grantType").asText();
        accessToken = om.readTree(response).get("accessToken").asText();

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
        AddMemberRequest request = new AddMemberRequest("id1", "1234", "user@email.com");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(request.getEmail());
        UpdateMemberRequest updateRequest = new UpdateMemberRequest("id2","5678", "member@email.com");
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(request.getEmail(), "123456");
        ResultActions result;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(singUpResponse).get("grantType").asText();
        String accessToken = om.readTree(singUpResponse).get("accessToken").asText();
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(request)).header("Authorization",grantType + " " + accessToken)).andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(response).get("grantType").asText();
        accessToken = om.readTree(response).get("accessToken").asText();

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
        AddMemberRequest addRequest = new AddMemberRequest("id1", "1234", "user@email.com");
        SendToEmailMemberRequest emailRequest = new SendToEmailMemberRequest(addRequest.getEmail());
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(addRequest.getEmail(), "123456");
        ResultActions result1, result2;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(emailRequest)));
        String singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        String grantType = om.readTree(singUpResponse).get("grantType").asText();
        String accessToken = om.readTree(singUpResponse).get("accessToken").asText();
        String response = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRequest)).header("Authorization",grantType + " " + accessToken)).andReturn().getResponse().getContentAsString();
        String username = om.readTree(response).get("username").asText();
        response = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(new LoginMemberRequest(username, "1234")))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(response).get("grantType").asText();
        accessToken = om.readTree(response).get("accessToken").asText();

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
        AddMemberRequest addMemberRequest = new AddMemberRequest("id1", "password1", "id1@email.com");
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(addMemberRequest.getEmail(), "123457");

        ResultActions result1 = mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(requestEmail)));
        ResultActions result2 = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest)));

        result1
                .andExpect(status().isOk());
        result2
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void authorizationForbiddenTest() throws Exception {
        SendToEmailMemberRequest requestEmail = new SendToEmailMemberRequest("choda1510@kyungmin.ac.kr");
        AddMemberRequest addMemberRequest = new AddMemberRequest("id1", "password1", "choda1510@kyungmin.ac.kr");
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(addMemberRequest.getEmail(), "123456");
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest("id1", "password1", "choda1510@kyungmin.ac.kr");
        String singUpResponse, grantType, accessToken;
        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(requestEmail)));
        singUpResponse = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        grantType = om.readTree(singUpResponse).get("grantType").asText();
        accessToken = om.readTree(singUpResponse).get("accessToken").asText();
        mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addMemberRequest)).header("Authorization", grantType + " " + accessToken));

        ResultActions result1 = mockMvc.perform(put("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateMemberRequest)).header("Authorization", grantType + " " + accessToken));

        result1
                .andExpect(status().isForbidden());
    }
}
