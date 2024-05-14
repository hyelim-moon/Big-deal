package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.AddMemberRequest;
import com.capstone.dto.member.LoginMemberRequest;
import com.capstone.dto.member.MemberResponse;
import com.capstone.dto.member.UpdateMemberRequest;
import com.capstone.exception.*;
import com.capstone.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;


@Transactional
@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void findByIdTest() {
        MemberResponse savedMember = service.insert(new AddMemberRequest("id","password","member@email.com"));
        MemberResponse saveMember = service.findAll().stream().filter((o)->o.getUsername().equals(savedMember.getUsername())).toList().get(0);
        MemberResponse member = service.findById(saveMember.getUuid());
        Assertions.assertEquals(savedMember.getEmail(), member.getEmail());
    }
    @Test
    public void findByIdNegativeTest() {
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.findById("this is not exist uuid"));
    }
    @Test
    public void insertTest() {
        MemberResponse response = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        Assertions.assertEquals("id1", response.getUsername());
        Assertions.assertNotNull(response.getSingUpDateTime());
        Assertions.assertNull(response.getSingOutDateTime());
    }
    @Test
    public void insertDuplicateTest() {
        service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.insert(new AddMemberRequest("id2", "password2", "member2@email.com"));
        Assertions.assertThrows(MemberUsernameDuplicateException.class, ()->service.insert(new AddMemberRequest("id1", "password1", "member3@email.com")));
    }
    @Test
    public void insertEmailDuplicateTest() {
        service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.insert(new AddMemberRequest("id2", "password2", "member2@email.com"));
        Assertions.assertThrows(MemberEmailDuplicateException.class, ()->service.insert(new AddMemberRequest("id3", "password3", "member1@email.com")));
    }
    @Test
    public void insertNegativeTest() {
        Assertions.assertThrows(NullPointerException.class, ()->service.insert(null));
        Assertions.assertThrows(RuntimeException.class, ()->service.insert(new AddMemberRequest(null, null, null)));
        Assertions.assertThrows(RuntimeException.class, ()->service.insert(new AddMemberRequest("id1", null, null)));
    }
    @Test
    public void updateTest() {
        MemberResponse saveBefore = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        UpdateMemberRequest request = new UpdateMemberRequest(null, "password1","member2@email.com");
        MemberResponse response = service.update(saveBefore.getUuid(), request);
        MemberResponse saveAfter = service.findById(saveBefore.getUuid());
        Assertions.assertEquals(saveBefore.getUsername(), response.getUsername());
        Assertions.assertEquals(saveAfter.getEmail(), response.getEmail());
        Assertions.assertEquals(saveAfter.getSingUpDateTime(), saveBefore.getSingUpDateTime());
    }
    @Test
    public void updateNotFoundTest() {
        Assertions.assertThrows(MemberNotFoundException.class ,()->service.update("user0", new UpdateMemberRequest("username1", "password2", "member3@email.com")));
    }
    @Test
    public void updateDuplicateTest() {
        MemberResponse member1Response = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        MemberResponse member2Response = service.insert(new AddMemberRequest("id2", "password2", "member2@email.com"));
        Assertions.assertThrows(MemberUsernameDuplicateException.class, ()->service.update(member1Response.getUuid(), new UpdateMemberRequest(member2Response.getUsername(), "password", member2Response.getEmail())));
    }
    @Test
    public void deleteTest() {
        MemberResponse response = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.delete(response.getUuid());
        Assertions.assertThrows(MemberNotFoundException.class,()->service.findById(response.getUuid()));
    }
    @Test
    public void deleteNotFoundTest() {
        Assertions.assertThrows(MemberNotFoundException.class, () -> service.delete("not exist"));
    }
    @Test
    public void withdrawalTest() {
        MemberResponse response = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.withdrawal(response.getUuid());
        Assertions.assertNotNull(service.findById(response.getUuid()).getSingOutDateTime());
    }
    @Test
    public void withdrawalNotFoundTest() {
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.withdrawal("not exist"));
    }
    @Test
    public void loginTest() {
        MemberResponse response = service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        LoginMemberRequest request = new LoginMemberRequest("id1", "password1");
        JwtTokenResponse token = service.login(request);
        Assertions.assertEquals(response.getUuid(), jwtTokenProvider.getUsername(token.getAccessToken()));
    }
    @Test
    public void loginBadRequest() {
        LoginMemberRequest request = new LoginMemberRequest("", "");
        Assertions.assertThrows(MemberBadRequestException.class, ()->service.login(request));
    }
    @Test
    public void loginFailTest() {
        service.insert(new AddMemberRequest("id1", "password1", "member1@email.com"));
        LoginMemberRequest request = new LoginMemberRequest("id1", "password2");
        Assertions.assertThrows(MemberPasswordNotEqualsException.class, ()->service.login(request));
    }
    @Test
    public void loginNotFoundTest() {
        LoginMemberRequest request = new LoginMemberRequest("id1", "password1");
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.login(request));
    }
}
