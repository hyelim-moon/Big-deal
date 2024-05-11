package com.capstone.controller;

import com.capstone.dto.franchise.FranchiseResponse;
import com.capstone.service.franchise.FranchiseNotFoundException;
import com.capstone.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name="가맹점 관련 서비스 접근 api",description=
        "<p>지역화폐가맹점을 검색하거나 관리할 때 사용하는 rest api 입니다.</p>")
@RestController
@RequestMapping(value="/api/v1/franchise")
public class FranchiseRestController {
    @Autowired
    private FranchiseService service;
    @Operation(summary = "가맹점 확인", description = "가맹점의 uuid 값으로 해당 가맹점 정보만 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description = "Success", content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    })
    @GetMapping(value="{uuid}")
    public ResponseEntity<FranchiseResponse> findById(
            @Schema(description = "가맹점의 uuid 입니다.")
            @PathVariable("uuid")
            String uuid) {
       return ResponseEntity.ok().body(service.findById(uuid));
    }
//    @GetMapping(value="")
//    public ResponseEntity<List<Franchise>> search(@RequestParam BigDecimal fromLa, @RequestParam BigDecimal toLa, @RequestParam BigDecimal fromLo, @RequestParam BigDecimal toLo) {
//        return ResponseEntity.ok().body(service.findByLatitudeAndLongitude(fromLa, toLa, fromLo, toLo));
//    }
    @Operation(summary = "가맹점 검색", description = "인자로 넘긴 위도와 경도에 가까운 순서로 모든 가맹점 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = FranchiseResponse.class)))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    })
    @GetMapping(value="")
    public ResponseEntity<List<FranchiseResponse>> findAll(
            @Parameter(name = "la", description = "위도(latitude)", required = true)
            @RequestParam
            BigDecimal la,

            @Parameter(name = "lo", description = "경도(longitude)", required = true)
            @RequestParam
            BigDecimal lo) {
        return ResponseEntity.ok().body(service.findByCenter(la, lo));
    }
}
