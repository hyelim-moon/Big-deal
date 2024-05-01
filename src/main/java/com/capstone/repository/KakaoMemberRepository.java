package com.capstone.repository;

import com.capstone.entity.KakaoMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoMemberRepository extends JpaRepository<KakaoMember, Long> {
}
