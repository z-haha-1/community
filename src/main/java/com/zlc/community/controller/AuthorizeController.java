package com.zlc.community.controller;

import com.zlc.community.dto.AccessTokenDTO;
import com.zlc.community.dto.GithubUser;
import com.zlc.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId; // 注入

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.client.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        //accessTokenDTO.setClient_id("0ca8e55ac030a10ba611");
        //accessTokenDTO.setClient_secret("4ca88713d478b400bbefc016dea02ee75759e97d");
        //accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");

        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        System.out.println(user.getId());
        // System.out.println(user.getBio());
        return "index";
    }

}
