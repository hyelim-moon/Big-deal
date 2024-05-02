package com.capstone.controller;

import com.capstone.entity.Franchise;
import com.capstone.service.FranchiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/franchise")
public class FranchiseRestController {
    @Autowired
    private FranchiseService service;

    @GetMapping(value="sector/{sector}")
    public List<Franchise> searchBySector() {
        List<Franchise> list = null;
        return list;
    }
    @GetMapping(value="")
    public List<Franchise> search(@RequestParam String fromLa, @RequestParam String toLa, @RequestParam String fromLo, @RequestParam String toLo) {
        return service.findByLatitudeAndLongitude(new BigDecimal(fromLa), new BigDecimal(toLa), new BigDecimal(fromLo), new BigDecimal(toLo));
    }
}
