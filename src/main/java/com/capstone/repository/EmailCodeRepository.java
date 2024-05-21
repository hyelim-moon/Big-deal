package com.capstone.repository;

import com.capstone.entity.EmailCode;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Profile("dev")
@Repository
public interface EmailCodeRepository extends JpaRepository<EmailCode, String> {
}
