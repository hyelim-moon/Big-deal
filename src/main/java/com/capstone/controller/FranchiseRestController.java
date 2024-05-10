package com.capstone.controller;

import com.capstone.dto.franchise.FranchiseResponse;
import com.capstone.service.FranchiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/franchise")
public class FranchiseRestController {
    @Autowired
    private FranchiseService service;
    @GetMapping(value="{uuid}")
    public ResponseEntity<FranchiseResponse> findById(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok().body(service.findById(uuid));
    }
//    @GetMapping(value="")
//    public ResponseEntity<List<Franchise>> search(@RequestParam BigDecimal fromLa, @RequestParam BigDecimal toLa, @RequestParam BigDecimal fromLo, @RequestParam BigDecimal toLo) {
//        return ResponseEntity.ok().body(service.findByLatitudeAndLongitude(fromLa, toLa, fromLo, toLo));
//    }
    @GetMapping(value="")
    public ResponseEntity<List<FranchiseResponse>> findAll(@RequestParam BigDecimal la, @RequestParam BigDecimal lo) {
        return ResponseEntity.ok().body(service.findByCenter(la, lo));
    }
}
