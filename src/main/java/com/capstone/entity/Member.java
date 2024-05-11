package com.capstone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column
    private String uuid;
    @Column
    private String id;
    @Column
    private String password;
    @Column
    private String email;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime singUpDateTime;
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime singOutDateTime;;
    @Builder
    public Member(String id, String password, String email) {
        this.id = id;
        this.password = password;
        this.email = email;
    }
    public Member update(String id, String password, String email) {
        this.id = id;
        this.password = password;
        this.email = email;
        return this;
    }
}
