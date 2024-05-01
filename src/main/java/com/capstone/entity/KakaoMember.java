package com.capstone.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class KakaoMember {
    @Id
    @Column
    private Long id;
    @OneToOne
    @JoinColumn(name="uuid")
    private Member member;
    @Builder
    public KakaoMember(Long id, Member member) {
        this.id = id;
        this.member = member;
    }
}
