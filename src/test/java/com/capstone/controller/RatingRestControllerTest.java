package com.capstone.controller;

import com.capstone.dto.member.*;
import com.capstone.dto.rating.*;
import com.capstone.provider.PinNumberUtility;
import com.capstone.repository.MemberRepository;
import com.capstone.repository.RatingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.hamcrest.Matchers;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class RatingRestControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RatingRepository ratingRepository;
    private final ObjectMapper om = new ObjectMapper();
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private PinNumberUtility pinNumberUtility;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        ratingRepository.deleteAll();
        memberRepository.deleteAll();
        doReturn("123456").when(pinNumberUtility).createCode();
    }
    @AfterEach
    public void afterEach() {
        ratingRepository.deleteAll();
        memberRepository.deleteAll();
    }
    private String memberSignup(String username, String password, String email) throws Exception {
        AddMemberControllerRequest addMemberRequest = new AddMemberControllerRequest(username, password);
        SendToEmailMemberRequest sendToEmailMemberRequest = new SendToEmailMemberRequest(email);
        VerifiedMemberRequest verifiedMemberRequest = new VerifiedMemberRequest(email, "123456");

        mockMvc.perform(post("/api/v1/member/auth/email").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(sendToEmailMemberRequest)));
        String responseCode = mockMvc.perform(post("/api/v1/member/auth/code").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(verifiedMemberRequest))).andReturn().getResponse().getContentAsString();
        String accessTokenOfAdd = om.readTree(responseCode).get("accessToken").asText();

        String responseSignup = mockMvc.perform(post("/api/v1/member").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addMemberRequest)).header("Authorization", "Bearer " + accessTokenOfAdd)).andReturn().getResponse().getContentAsString();
        return om.readTree(responseSignup).get("uuid").asText();
    }
    private String memberLogin(String username, String password) throws Exception {
        LoginMemberRequest loginMemberRequest = new LoginMemberRequest(username, password);
        String responseLogin1 = mockMvc.perform(post("/api/v1/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(loginMemberRequest))).andReturn().getResponse().getContentAsString();
        return om.readTree(responseLogin1).get("accessToken").asText();
    }
    @Test
    public void insertTest() throws Exception {
        String member1Uuid, accessToken1;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        System.out.println("Bearer " + accessToken1);
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest( "1", 7, "review1");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberUuid").value(member1Uuid));
    }
    @Test
    public void insertEmptyValueTest() throws Exception {
        String member1Uuid, member2Uuid, accessToken1, accessToken2;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        member2Uuid = memberSignup("id2", "5678", "id2@email.com");
        accessToken1 = memberLogin("id1", "1234");
        accessToken2 = memberLogin("id2", "5678");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest(null, 7, "review1");
        AddRatingControllerRequest addRatingRequest2 = new AddRatingControllerRequest("1", 7, null);
        AddRatingControllerRequest addRatingRequest3 = new AddRatingControllerRequest("1", null, "review3");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1));
        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest2)).header("Authorization", "Bearer " + accessToken2));
        ResultActions resultActions3 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest3)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isNotFound());
        resultActions2
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberUuid").value(member2Uuid));
        resultActions3
                .andExpect(status().isBadRequest());
    }
    @Test
    public void insertDuplicateTest() throws Exception {
        String member1Uuid, accessToken1;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        AddRatingControllerRequest addRatingRequest2 = new AddRatingControllerRequest("1", 8, "review2");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1));
        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest2)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isCreated());
        resultActions2
                .andExpect(status().isForbidden());
    }
    @Test
    public void insertMemberNotExistsTest() throws Exception {
        String member1Uuid, accessToken1;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        mockMvc.perform(delete("/api/v1/member").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken1));
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void insertFranchiseNotExistsTest() throws Exception {
        String member1Uuid, accessToken1;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("not exists", 7, "review1");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isNotFound());
    }
    @Test
    public void insertUnauthorizedTest() throws Exception {
        String member1Uuid, accessToken1;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void updateTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest(rating1Uuid, 8, "review 1");

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starRating").value(updateRatingRequest1.getStarRating()))
                .andExpect(jsonPath("$.review").value(updateRatingRequest1.getReview()));
    }
    @Test
    public void updateNullTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest(rating1Uuid, null, "review 1");

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starRating").value(addRatingRequest1.getStarRating()))
                .andExpect(jsonPath("$.review").value(updateRatingRequest1.getReview()));
    }
    @Test
    public void updateExclusiveTest() throws Exception {
        String member1Uuid, member2Uuid, accessToken1, accessToken2, rating1Uuid, rating2Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        member2Uuid = memberSignup("id2", "5678", "id2@email.com");
        accessToken1 = memberLogin("id1", "1234");
        accessToken2 = memberLogin("id2", "5678");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        AddRatingControllerRequest addRatingRequest2 = new AddRatingControllerRequest("1", 5, "review2");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        String responseRating2 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken2)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        rating2Uuid = om.readTree(responseRating2).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest(rating2Uuid, 8, "review 1");

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isForbidden());
    }
    @Test
    public void updateMemberNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest(rating1Uuid, 8, "review 1");
        mockMvc.perform(delete("/api/v1/member").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken1));

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void updateRatingNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest("not exists", 8, "review 1");

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isNotFound());
    }
    @Test
    public void updateUnauthorizedTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        UpdateRatingControllerRequest updateRatingRequest1 = new UpdateRatingControllerRequest(rating1Uuid, 8, "review 1");

        ResultActions resultActions1 = mockMvc.perform(put("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(updateRatingRequest1)));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void findByIdTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/" + rating1Uuid).contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starRating").value(addRatingRequest1.getStarRating()))
                .andExpect(jsonPath("$.review").value(addRatingRequest1.getReview()));
    }
    @Test
    public void findByIdRatingNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/notExists").contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isNotFound());
    }
    @Test
    public void findByMemberUuidTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/member/" + member1Uuid).contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].starRating").value(addRatingRequest1.getStarRating()))
                .andExpect(jsonPath("$[0].review").value(addRatingRequest1.getReview()));
    }
    @Test
    public void findByMemberUuidNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/member/notExists").contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
    @Test
    public void findByFranchiseUuidTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/franchise/1").contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].franchiseUuid").value("1"));
    }
    @Test
    public void findByFranchiseUuidNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();

        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/rating/franchise/notExists").contentType(MediaType.APPLICATION_JSON));

        resultActions1
                .andExpect(status().isNotFound());
    }
    @Test
    public void removeTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        RemoveRatingRequest removeRatingRequest1 = new RemoveRatingRequest(rating1Uuid);

        ResultActions resultActions1 = mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)).header("Authorization", "Bearer " + accessToken1));
        ResultActions resultActions2 = mockMvc.perform(get("/api/v1/rating/" + rating1Uuid));

        resultActions1
                .andExpect(status().isOk());
        resultActions2
                .andExpect(status().isNotFound());
    }
    @Test
    public void removeRatingNotExistsTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        RemoveRatingRequest removeRatingRequest1 = new RemoveRatingRequest("not exists");

        ResultActions resultActions1 = mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isNotFound());
    }
    @Test
    public void removeExclusiveTest() throws Exception {
        String member1Uuid, member2Uuid, accessToken1, accessToken2, rating1Uuid, rating2Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        member2Uuid = memberSignup("id2", "5678", "id2@email.com");
        accessToken1 = memberLogin("id1", "1234");
        accessToken2 = memberLogin("id2", "5678");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        AddRatingControllerRequest addRatingRequest2 = new AddRatingControllerRequest("1", 9, "review2");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        String responseRating2 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken2)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        rating2Uuid = om.readTree(responseRating2).get("uuid").asText();
        RemoveRatingRequest removeRatingRequest1 = new RemoveRatingRequest(rating2Uuid);

        ResultActions resultActions1 = mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isForbidden());
    }
    @Test
    public void removeDuplicateTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        RemoveRatingRequest removeRatingRequest1 = new RemoveRatingRequest(rating1Uuid);
        mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        ResultActions resultActions1 = mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)).header("Authorization", "Bearer " + accessToken1));

        resultActions1
                .andExpect(status().isForbidden());
    }
    @Test
    public void removeUnauthorizedTest() throws Exception {
        String member1Uuid, accessToken1, rating1Uuid;
        member1Uuid = memberSignup("id1", "1234", "id1@email.com");
        accessToken1 = memberLogin("id1", "1234");
        AddRatingControllerRequest addRatingRequest1 = new AddRatingControllerRequest("1", 7, "review1");
        String responseRating1 = mockMvc.perform(post("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(addRatingRequest1)).header("Authorization", "Bearer " + accessToken1)).andReturn().getResponse().getContentAsString();
        rating1Uuid = om.readTree(responseRating1).get("uuid").asText();
        RemoveRatingRequest removeRatingRequest1 = new RemoveRatingRequest(rating1Uuid);

        ResultActions resultActions1 = mockMvc.perform(delete("/api/v1/rating").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(removeRatingRequest1)));

        resultActions1
                .andExpect(status().isUnauthorized());
    }
}
