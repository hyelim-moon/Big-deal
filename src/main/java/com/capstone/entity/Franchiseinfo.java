package com.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "franchise_info")
public class Franchiseinfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brno")
    private String brno;

    @Column(name = "bzmn_stts")
    private String bzmnStts;

    @Column(name = "emd_nm")
    private String emdNm;

    @Column(name = "frcs_addr")
    private String frcsAddr;

    @Column(name = "frcs_dtl_addr")
    private String frcsDtlAddr;

    @Column(name = "frcs_nm")
    private String frcsNm;

    @Column(name = "frcs_reg_se")
    private String frcsRegSe;

    @Column(name = "frcs_reg_se_nm")
    private String frcsRegSeNm;

    @Column(name = "frcs_rprs_telno")
    private String frcsRprsTelno;

    @Column(name = "frcs_stlm_info_se")
    private String frcsStlmInfoSe;

    @Column(name = "frcs_stlm_info_se_nm")
    private String frcsStlmInfoSeNm;

    @Column(name = "usage_rgn_cd")
    private String usageRgnCd;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lot")
    private Double lot;
}
