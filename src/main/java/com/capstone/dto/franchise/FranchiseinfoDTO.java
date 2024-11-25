package com.capstone.dto.franchise;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseinfoDTO {
    private String brno;
    private String bzmnStts;
    private String emdNm;
    private String frcsAddr;
    private String frcsDtlAddr;
    private String frcsNm;
    private String frcsRegSe;
    private String frcsRegSeNm;
    private String frcsRprsTelno;
    private String frcsStlmInfoSe;
    private String frcsStlmInfoSeNm;
    private String usageRgnCd;
    private Double lat;
    private Double lot;
}
