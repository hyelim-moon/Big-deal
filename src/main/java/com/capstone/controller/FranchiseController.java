package com.capstone.controller;

import com.capstone.dto.franchise.FranchiseinfoDTO;
import com.capstone.service.franchise.FranchiseService;
import com.capstone.dto.franchise.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/franchises")
public class FranchiseController {
    private final FranchiseService franchiseService;

    @GetMapping("/load")
    public ApiResponse<?> loadFranchisesFromApi() {
        int totalLoadedCount = 0;

        // 사용할 usageRgnCd 값 목록
        String[] usageRgnCdList = {
                "41110", "41111", "41113", "41115", "41117", "41130",
                "41131", "41133", "41135", "41150", "41170", "41171",
                "41173", "41190", "41195", "41197", "41199"
        };

        try {
            for (String usageRgnCd : usageRgnCdList) {
                int usageRgnCdTotalCount = 0;
                String serviceKey = "2Wp0sBIqsx1shZYCs/6VGCfG5zME2h3SQ+qVCMXH1MWusf1V6xSdAr5Q8xINQ0zys6E/s0LA7cHJgIX2neEKEA==";
                int page = 1;
                int perPage = 5000;

                while (true) {
                    // URL 생성
                    String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
                    String encodedUsageRgnCd = URLEncoder.encode(usageRgnCd, "UTF-8");
                    String encodedCond = URLEncoder.encode("[usage_rgn_cd::EQ]", "UTF-8");
                    String urlString = String.format(
                            "https://apis.data.go.kr/B190001/localFranchisesV2/franchiseV2?serviceKey=%s&page=%d&perPage=%d&cond%s=%s",
                            encodedServiceKey,
                            page,
                            perPage,
                            encodedCond,
                            encodedUsageRgnCd
                    );

                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-type", "application/json");

                    // 응답 읽기
                    BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    String result = bf.readLine();
                    bf.close();

                    // JSON 파싱
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(result);

                    // totalCount 확인
                    Long totalCount = (Long) jsonObject.get("totalCount");
                    if (totalCount == null) {
                        log.error("totalCount is missing in the response for usageRgnCd: {}", usageRgnCd);
                        break;
                    }

                    // 데이터 처리
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    if (jsonArray == null || jsonArray.isEmpty()) {
                        break; // 더 이상 데이터가 없으면 종료
                    }

                    int savedCount = saveFranchiseData(jsonArray);
                    totalLoadedCount += savedCount;
                    usageRgnCdTotalCount += savedCount;

                    log.info("Loaded {} records for usageRgnCd: {} on page {}", savedCount, usageRgnCd, page);

                    // 다음 페이지로 이동
                    page++;
                }

                log.info("usageRgnCd: {} - 총 {} 개의 프랜차이즈 데이터 로드 완료", usageRgnCd, usageRgnCdTotalCount);
            }

            // ApiResponse 성공 응답 반환
            return ApiResponse.success(
                    null,
                    HttpStatus.OK,
                    "프랜차이즈 데이터 로딩 성공",
                    totalLoadedCount
            );

        } catch (Exception e) {
            log.error("프랜차이즈 데이터 로딩 중 오류 발생", e);

            // ApiResponse 에러 응답 반환
            return ApiResponse.error(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "데이터 로딩 중 오류 발생: " + e.getMessage()
            );
        }
    }

    private int saveFranchiseData(JSONArray jsonArray) {
        int savedCount = 0;
        for (Object obj : jsonArray) {
            JSONObject franchiseObj = (JSONObject) obj;

            FranchiseinfoDTO franchiseDto = FranchiseinfoDTO.builder()
                    .brno((String) franchiseObj.get("brno"))
                    .bzmnStts((String) franchiseObj.get("bzmn_stts"))
                    .emdNm((String) franchiseObj.get("emd_nm"))
                    .frcsAddr((String) franchiseObj.get("frcs_addr"))
                    .frcsDtlAddr((String) franchiseObj.get("frcs_dtl_addr"))
                    .frcsNm((String) franchiseObj.get("frcs_nm"))
                    .frcsRegSe((String) franchiseObj.get("frcs_reg_se"))
                    .frcsRegSeNm((String) franchiseObj.get("frcs_reg_se_nm"))
                    .frcsRprsTelno((String) franchiseObj.get("frcs_rprs_telno"))
                    .frcsStlmInfoSe((String) franchiseObj.get("frcs_stlm_info_se"))
                    .frcsStlmInfoSeNm((String) franchiseObj.get("frcs_stlm_info_se_nm"))
                    .usageRgnCd((String) franchiseObj.get("usage_rgn_cd"))
                    .lat(franchiseObj.get("lat") != null ? Double.parseDouble(franchiseObj.get("lat").toString()) : null)
                    .lot(franchiseObj.get("lot") != null ? Double.parseDouble(franchiseObj.get("lot").toString()) : null)
                    .build();

            franchiseService.save(franchiseDto);
            savedCount++;
        }
        return savedCount;
    }

    @GetMapping("/map")
    public ApiResponse<?> getFranchisesForMap() {
        List<FranchiseinfoDTO> franchises = franchiseService.findAll();
        return ApiResponse.success(null, HttpStatus.OK, "프랜차이즈 위치 데이터", franchises);
    }
}
