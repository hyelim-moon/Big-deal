package com.capstone.controller;

import com.capstone.dto.FranchiseInfoResponse;
import com.capstone.entity.Franchise;
import com.capstone.service.FranchiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/franchise")
public class FranchiseRestController {
    @Autowired
    private FranchiseService service;
    @GetMapping(value="{uuid}")
    public ResponseEntity<FranchiseInfoResponse> findById(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok().body(service.findById(uuid));
    }

    @GetMapping(value="sector/{sector}")
    public List<Franchise> searchBySector() {
        List<Franchise> list = null;
        return list;
    }
    @GetMapping(value="")
    public ResponseEntity<List<Franchise>> search(@RequestParam String fromLa, @RequestParam String toLa, @RequestParam String fromLo, @RequestParam String toLo) {
        return ResponseEntity.ok().body(service.findByLatitudeAndLongitude(new BigDecimal(fromLa), new BigDecimal(toLa), new BigDecimal(fromLo), new BigDecimal(toLo)));
    }
}
