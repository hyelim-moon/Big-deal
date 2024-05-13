package com.capstone.service;

import com.capstone.dto.member.AddMemberRequest;
import com.capstone.dto.member.MemberResponse;
import com.capstone.exception.MemberNotFoundException;
import com.capstone.exception.MemberUsernameDuplicateException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;


@Transactional
@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberService service;

    @Test
    public void findByIdTest() {
        MemberResponse savedMember = service.save(new AddMemberRequest("id","password","member@email.com"));
        MemberResponse saveMember = service.findAll().stream().filter((o)->o.getUsername().equals(savedMember.getUsername())).toList().get(0);
        MemberResponse member = service.findById(saveMember.getUuid());
        Assertions.assertEquals(savedMember.getEmail(), member.getEmail());
    }
    @Test
    public void findByIdNegativeTest() {
        Assertions.assertThrows(MemberNotFoundException.class, ()->service.findById("this is not exist uuid"));
    }
    @Test
    public void saveTest() {
        MemberResponse response = service.save(new AddMemberRequest("id1", "password1", "member1@email.com"));
        Assertions.assertEquals("id1", response.getUsername());
        Assertions.assertNotNull(response.getSingUpDateTime());
        Assertions.assertNull(response.getSingOutDateTime());
    }
    @Test
    public void saveDuplicateTest() {
        service.save(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.save(new AddMemberRequest("id2", "password2", "member2@email.com"));
        Assertions.assertThrows(MemberUsernameDuplicateException.class, ()->service.save(new AddMemberRequest("id1", "password1", "member1@email.com")));
    }
    @Test
    public void saveNegativeTest() {
        Assertions.assertThrows(NullPointerException.class, ()->service.save(null));
        Assertions.assertThrows(BadCredentialsException.class, ()->service.save(new AddMemberRequest(null, null, null)));
        Assertions.assertThrows(BadCredentialsException.class, ()->service.save(new AddMemberRequest("id1", null, null)));
    }
}
