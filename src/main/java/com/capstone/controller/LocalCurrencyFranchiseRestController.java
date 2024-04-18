package com.capstone.controller;

import com.capstone.entity.LocalCurrencyFranchise;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/lcfranchs")
public class LocalCurrencyFranchiseRestController {
    @GetMapping(value="sector/{sector}")
    public List<LocalCurrencyFranchise> searchBySector() {
        List<LocalCurrencyFranchise> list = null;
        return list;
    }
}
