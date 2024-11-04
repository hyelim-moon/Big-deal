package com.capstone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/login")
public class KakaoLoginPageController {
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/page")
    public String loginPage() {
        return "loginPage";  // templates/loginPage.html을 렌더링
    }

    // 카카오 로그인 URL을 반환하는 API
    @GetMapping("/kakao-url")
    @ResponseBody
    public String getKakaoLoginUrl(@RequestParam(value = "redirect_uri", required = false) String redirectUri) {
        String state = redirectUri != null ? redirectUri : "http://localhost:8080/html/newmapPage.html"; // default to mapPage
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + client_id
                + "&redirect_uri=" + redirect_uri
                + "&state=" + state;
    }
}