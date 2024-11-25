package com.capstone.service.franchise;

import com.capstone.dto.franchise.FranchiseinfoDTO;
import com.capstone.entity.Franchiseinfo;
import com.capstone.repository.FranchiseInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FranchiseService {
    private final FranchiseInfoRepository franchiseRepository;

    @Transactional
    public FranchiseinfoDTO save(FranchiseinfoDTO franchiseDto) {
        Franchiseinfo franchise = Franchiseinfo.builder()
                .brno(franchiseDto.getBrno())
                .bzmnStts(franchiseDto.getBzmnStts())
                .emdNm(franchiseDto.getEmdNm())
                .frcsAddr(franchiseDto.getFrcsAddr())
                .frcsDtlAddr(franchiseDto.getFrcsDtlAddr())
                .frcsNm(franchiseDto.getFrcsNm())
                .frcsRegSe(franchiseDto.getFrcsRegSe())
                .frcsRegSeNm(franchiseDto.getFrcsRegSeNm())
                .frcsRprsTelno(franchiseDto.getFrcsRprsTelno())
                .frcsStlmInfoSe(franchiseDto.getFrcsStlmInfoSe())
                .frcsStlmInfoSeNm(franchiseDto.getFrcsStlmInfoSeNm())
                .usageRgnCd(franchiseDto.getUsageRgnCd())
                .lat(franchiseDto.getLat())
                .lot(franchiseDto.getLot())
                .build();

        Franchiseinfo savedFranchise = franchiseRepository.save(franchise);

        return FranchiseinfoDTO.builder()
                .brno(savedFranchise.getBrno())
                .bzmnStts(savedFranchise.getBzmnStts())
                .emdNm(savedFranchise.getEmdNm())
                .frcsAddr(savedFranchise.getFrcsAddr())
                .frcsDtlAddr(savedFranchise.getFrcsDtlAddr())
                .frcsNm(savedFranchise.getFrcsNm())
                .frcsRegSe(savedFranchise.getFrcsRegSe())
                .frcsRegSeNm(savedFranchise.getFrcsRegSeNm())
                .frcsRprsTelno(savedFranchise.getFrcsRprsTelno())
                .frcsStlmInfoSe(savedFranchise.getFrcsStlmInfoSe())
                .frcsStlmInfoSeNm(savedFranchise.getFrcsStlmInfoSeNm())
                .usageRgnCd(savedFranchise.getUsageRgnCd())
                .lat(savedFranchise.getLat())
                .lot(savedFranchise.getLot())
                .build();
    }

    @Transactional(readOnly = true)
    public List<FranchiseinfoDTO> findAll() {
        return franchiseRepository.findAll().stream()
                .map(franchise -> FranchiseinfoDTO.builder()
                        .brno(franchise.getBrno())
                        .bzmnStts(franchise.getBzmnStts())
                        .emdNm(franchise.getEmdNm())
                        .frcsAddr(franchise.getFrcsAddr())
                        .frcsDtlAddr(franchise.getFrcsDtlAddr())
                        .frcsNm(franchise.getFrcsNm())
                        .frcsRegSe(franchise.getFrcsRegSe())
                        .frcsRegSeNm(franchise.getFrcsRegSeNm())
                        .frcsRprsTelno(franchise.getFrcsRprsTelno())
                        .frcsStlmInfoSe(franchise.getFrcsStlmInfoSe())
                        .frcsStlmInfoSeNm(franchise.getFrcsStlmInfoSeNm())
                        .usageRgnCd(franchise.getUsageRgnCd())
                        .lat(franchise.getLat())
                        .lot(franchise.getLot())
                        .build())
                .collect(Collectors.toList());
    }
}