package com.capstone.service;

import com.capstone.dto.AddMemberRequest;
import com.capstone.dto.MemberInfoResponse;
import com.capstone.dto.MemberResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberService service;

    @Test
    public void findByIdTest() {
        MemberInfoResponse savedMember = service.save(new AddMemberRequest("id","password","member@email.com"));
        MemberResponse saveMember = service.findAll().stream().filter((o)->o.getId().equals(savedMember.getId())).toList().get(0);
        MemberInfoResponse member = service.findById(saveMember.getUuid());
        Assertions.assertEquals(savedMember.getEmail(), member.getEmail());
    }
    @Test
    public void findByIdNegativeTest() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.findById("this is not exist uuid"));
    }
    @Test
    public void saveDuplicateTest() {
        service.save(new AddMemberRequest("id1", "password1", "member1@email.com"));
        service.save(new AddMemberRequest("id2", "password2", "member2@email.com"));
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.save(new AddMemberRequest("id1", "password1", "member1@email.com")));
    }
    @Test
    public void saveNegativeTest() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.save(null));
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.save(new AddMemberRequest(null, null, null)));
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.save(new AddMemberRequest("id1", null, null)));
    }
}
