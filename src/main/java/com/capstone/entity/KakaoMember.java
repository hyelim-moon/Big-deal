package com.capstone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoMember {
    @Id
    @Column
    private Long id;
    @OneToOne
    @JoinColumn(name="uuid")
    private Member member;
}
