package com.capstone.service.franchise;

import com.capstone.dto.franchise.NewFranchiseRequest;
import com.capstone.entity.NewFranchise;
import com.capstone.repository.NewFranchiseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewFranchiseService {
    private final NewFranchiseRepository newFranchiseRepository;

    @Transactional
    public NewFranchise saveNewFranchise(NewFranchiseRequest request) {
        // 중복 체크
        if (newFranchiseRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new DuplicateKeyException("이미 등록된 가맹점입니다.");
        }

        // 데이터 유효성 검증
        validateNewFranchiseRequest(request);

        // Entity 생성 및 저장
        NewFranchise newFranchise = new NewFranchise();
        newFranchise.setName(request.getName());
        newFranchise.setAddress(request.getAddress());
        newFranchise.setSector(request.getSector());
        newFranchise.setLatitude(request.getLatitude());
        newFranchise.setLongitude(request.getLongitude());
        newFranchise.setCurrencies(request.getCurrencies());

        return newFranchiseRepository.save(newFranchise);
    }

    private void validateNewFranchiseRequest(NewFranchiseRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("가맹점 이름은 필수입니다.");
        }
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }
        if (request.getSector() == null || request.getSector().trim().isEmpty()) {
            throw new IllegalArgumentException("업종은 필수입니다.");
        }
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("위치 정보는 필수입니다.");
        }
        if (request.getCurrencies() == null || request.getCurrencies().isEmpty()) {
            throw new IllegalArgumentException("지역화폐 종류는 필수입니다.");
        }
    }

}
