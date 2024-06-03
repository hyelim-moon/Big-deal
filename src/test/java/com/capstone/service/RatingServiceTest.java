package com.capstone.service;

import com.capstone.dto.member.AddMemberRequest;
import com.capstone.dto.member.MemberResponse;
import com.capstone.dto.rating.AddRatingRequest;
import com.capstone.dto.rating.RatingResponse;
import com.capstone.dto.rating.UpdateRatingRequest;
import com.capstone.exception.MemberNotFoundException;
import com.capstone.exception.RatingInvalidateUpdateException;
import com.capstone.exception.RatingNotFoundException;
import com.capstone.provider.PinNumberUtility;
import com.capstone.repository.FranchiseRepository;
import com.capstone.repository.MemberRepository;
import com.capstone.repository.RatingRepository;
import com.capstone.service.franchise.FranchiseNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.doReturn;

@SpringBootTest
public class RatingServiceTest {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private FranchiseService franchiseService;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private FranchiseRepository franchiseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private PinNumberUtility pinNumberUtility;
    @BeforeEach
    public void beforeEach() {
        ratingRepository.deleteAll();
        memberRepository.deleteAll();
    }
    @AfterEach
    public void afterEach() {
        ratingRepository.deleteAll();
    }
    @Test
    public void findByIdTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));
        RatingResponse savedRating = ratingService.insert(new AddRatingRequest(member1.getUuid(), "1", 8, "review1"));

        RatingResponse rating = ratingService.findById(savedRating.getUuid());

        Assertions.assertEquals(savedRating.getMemberUuid(), rating.getMemberUuid());
        Assertions.assertEquals(savedRating.getFranchiseUuid(), rating.getFranchiseUuid());
        Assertions.assertEquals(savedRating.getStarRating(), rating.getStarRating());
        Assertions.assertEquals(savedRating.getReview(), rating.getReview());
        Assertions.assertEquals(savedRating.getRegisterDateTime(), rating.getRegisterDateTime());
        Assertions.assertNull(rating.getDeleteDateTime());
    }
    @Test
    public void findByIdNotFoundTest() {
        Assertions.assertThrows(RatingNotFoundException.class, ()->ratingService.findById("not exists"));
    }
    @Test
    public void insertTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));

        RatingResponse rating = ratingService.insert(new AddRatingRequest(member1.getUuid(), "1", 8, "review1"));

        Assertions.assertEquals(member1.getUuid(), rating.getMemberUuid());
        Assertions.assertEquals("1", rating.getFranchiseUuid());
        Assertions.assertEquals(8, rating.getStarRating());
        Assertions.assertEquals("review1", rating.getReview());
    }
    @Test
    public void insertMemberNotFoundTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));

        Assertions.assertThrows(MemberNotFoundException.class, ()->ratingService.insert(new AddRatingRequest("not exist", "1", 8, "review1")));
    }
    @Test
    public void insertFranchiseNotFoundTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));

        Assertions.assertThrows(FranchiseNotFoundException.class, ()->ratingService.insert(new AddRatingRequest(member1.getUuid(), member2.getUuid(), 8, "review1")));
    }
    @Test
    public void updateTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));
        RatingResponse savedRating = ratingService.insert(new AddRatingRequest(member1.getUuid(), "1", 8, "review1"));

        RatingResponse updateRating = ratingService.update(member1.getUuid(), new UpdateRatingRequest(savedRating.getUuid(), 9, "review2"));

        Assertions.assertEquals(9, updateRating.getStarRating());
        Assertions.assertEquals("review2", updateRating.getReview());
    }
    @Test
    public void updateSomeTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));
        RatingResponse savedRating = ratingService.insert(new AddRatingRequest(member1.getUuid(), "1", 8, "review1"));

        RatingResponse updateRating = ratingService.update(member1.getUuid(), new UpdateRatingRequest(savedRating.getUuid(), 9, null));

        Assertions.assertEquals(9, updateRating.getStarRating());
        Assertions.assertEquals(savedRating.getReview(), updateRating.getReview());
    }
    @Test
    public void updateExclusiveTest() {
        MemberResponse member1 = memberService.insert(new AddMemberRequest("id1", "1234", "id1@email.com"));
        MemberResponse member2 = memberService.insert(new AddMemberRequest("id2", "1234", "id2@email.com"));
        RatingResponse member1Rating = ratingService.insert(new AddRatingRequest(member1.getUuid(), "1", 8, "review1"));
        RatingResponse member2Rating = ratingService.insert(new AddRatingRequest(member2.getUuid(), "1", 7, "review2"));

        Assertions.assertThrows(RatingInvalidateUpdateException.class, ()->ratingService.update(member1.getUuid(), new UpdateRatingRequest(member2Rating.getUuid(), 9, "review3")));
    }
}
