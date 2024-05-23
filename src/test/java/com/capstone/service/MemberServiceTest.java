package com.capstone.service;

import com.capstone.dto.JwtTokenResponse;
import com.capstone.dto.member.*;
import com.capstone.exception.*;
import com.capstone.provider.JwtTokenUtility;
import com.capstone.provider.PinNumberUtility;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.mockito.Mockito.doReturn;

@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Autowired
    private MemberService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtility jwtTokenUtility;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private PinNumberUtility pinNumberUtility;
    @BeforeEach
    public void beforeEach() {
        doReturn("123456").when(pinNumberUtility).createCode();
    }

    @Test
    public void findByIdTest() {
        AddMemberRequest request = new AddMemberRequest("id","password","member@email.com");
        service.sendCodeToEmail(request.getEmail());
        MemberResponse savedMember = service.insert(request);
        MemberResponse saveMember = service.findAll().stream().filter((o)->o.getUsername().equals(savedMember.getUsername())).toList().get(0);
        MemberResponse member = service.findById(saveMember.getUuid());

        Assertions.assertEquals(savedMember.getEmail(), member.getEmail());
    }
    @Test
    public void findByIdNegativeTest() {
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.findById("this is not exist uuid"));
    }
    @Test
    public void findAllTest() {
        List<MemberResponse> list = service.findAll();
        Assertions.assertFalse(list.isEmpty());
    }
    @Test
    public void sendCodeToEmailTest() {
        service.sendCodeToEmail("choda1510@kyungmin.ac.kr");
        MemberResponse saveMember = service.insert(new AddMemberRequest("id1", "password1", "choda1510@kyungmin.ac.kr"));
        Assertions.assertEquals("choda1510@kyungmin.ac.kr", saveMember.getEmail());
    }
    @Test
    public void sendCodeToEmailNegativeTest() {
        service.sendCodeToEmail("choda1510@kyungmin.ac.kr");
        Assertions.assertThrows(EmailInvalidateCodeException.class, ()->service.verifiedEmail(new VerifiedMemberRequest("choda1510@kyungmin.ac.kr", "123457")));
    }
    @Test
    public void insertTest() {
        AddMemberRequest request = new AddMemberRequest("id1", "password1", "user@email.com");
        service.sendCodeToEmail(request.getEmail());
        MemberResponse response = service.insert(request);
        Assertions.assertEquals("id1", response.getUsername());
        Assertions.assertNotNull(response.getSingUpDateTime());
        Assertions.assertNull(response.getWithdrawalDateTime());
    }
    @Test
    public void insertDuplicateTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        AddMemberRequest request2 = new AddMemberRequest("id2", "password2", "member2@email.com");
        AddMemberRequest request3 = new AddMemberRequest("id1", "password1", "member3@email.com");
        service.sendCodeToEmail(request1.getEmail());
        service.insert(request1);
        service.sendCodeToEmail(request2.getEmail());
        service.insert(request2);
        service.sendCodeToEmail(request3.getEmail());
        Assertions.assertThrows(MemberUsernameDuplicateException.class, ()->service.insert(request3));
    }
    @Test
    public void insertEmailDuplicateTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        AddMemberRequest request2 = new AddMemberRequest("id2", "password2", "member2@email.com");
        service.sendCodeToEmail(request1.getEmail());
        service.insert(request1);
        service.sendCodeToEmail(request2.getEmail());
        service.insert(request2);
        Assertions.assertThrows(MemberEmailDuplicateException.class, ()->service.sendCodeToEmail(request1.getEmail()));
    }
    @Test
    public void insertNegativeTest() {
        Assertions.assertThrows(NullPointerException.class, ()->service.insert(null));
        Assertions.assertThrows(RuntimeException.class, ()->service.insert(new AddMemberRequest(null, null, null)));
        Assertions.assertThrows(RuntimeException.class, ()->service.insert(new AddMemberRequest("id1", null, null)));
    }
    @Test
    public void updateTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse saveBefore = service.insert(request1);
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
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        AddMemberRequest request2 = new AddMemberRequest("id2", "password2", "member2@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse member1Response = service.insert(request1);
        service.sendCodeToEmail(request2.getEmail());
        MemberResponse member2Response = service.insert(request2);
        Assertions.assertThrows(MemberUsernameDuplicateException.class, ()->service.update(member1Response.getUuid(), new UpdateMemberRequest(member2Response.getUsername(), "password", member2Response.getEmail())));
    }
    @Test
    public void deleteTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        service.delete(response.getUuid());
        Assertions.assertThrows(MemberNotFoundException.class,()->service.findById(response.getUuid()));
    }
    @Test
    public void deleteNotFoundTest() {
        Assertions.assertThrows(MemberNotFoundException.class, () -> service.delete("not exist"));
    }
    @Test
    public void withdrawalTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        service.withdrawal(response.getUuid());
        Assertions.assertNotNull(service.findById(response.getUuid()).getWithdrawalDateTime());
    }
    @Test
    public void withdrawalNotFoundTest() {
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.withdrawal("not exist"));
    }
    @Test
    public void withdrawalRejectLoginTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        service.withdrawal(response.getUuid());
        Assertions.assertThrows(MemberInvalidateLoginException.class,()->service.login(new LoginMemberRequest("id1", "password1")));
    }
    @Test
    public void withdrawalRejectUpdateTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        service.withdrawal(response.getUuid());
        Assertions.assertThrows(MemberInvalidateUpdateException.class, ()->service.update(response.getUuid(), new UpdateMemberRequest(null, "password2", null)));
    }
    @Test
    public void usernameDuplicationExceptTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        Assertions.assertDoesNotThrow(()->service.update(response.getUuid() ,new UpdateMemberRequest(response.getUsername(), "password1", response.getEmail())));
    }
    @Test
    public void withdrawalTwiceTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        service.withdrawal(response.getUuid());
        Assertions.assertThrows(MemberBadRequestException.class, ()->service.withdrawal(response.getUuid()));
    }
    @Test
    public void loginTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        MemberResponse response = service.insert(request1);
        LoginMemberRequest request = new LoginMemberRequest("id1", "password1");
        JwtTokenResponse token = service.login(request);
        Assertions.assertEquals(response.getUuid(), jwtTokenUtility.getUsername(token.getAccessToken()));
    }
    @Test
    public void loginBadRequest() {
        LoginMemberRequest request = new LoginMemberRequest("", "");
        Assertions.assertThrows(MemberBadRequestException.class, ()->service.login(request));
    }
    @Test
    public void loginFailTest() {
        AddMemberRequest request1 = new AddMemberRequest("id1", "password1", "member1@email.com");
        service.sendCodeToEmail(request1.getEmail());
        service.insert(request1);
        LoginMemberRequest request = new LoginMemberRequest("id1", "password2");
        Assertions.assertThrows(MemberPasswordNotEqualsException.class, ()->service.login(request));
    }
    @Test
    public void loginNotFoundTest() {
        LoginMemberRequest request = new LoginMemberRequest("id1", "password1");
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.login(request));
    }
}
