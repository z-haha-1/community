package com.zlc.community.controller;

import com.zlc.community.dto.AccessTokenDTO;
import com.zlc.community.dto.GithubUser;
import com.zlc.community.mapper.UserMapper;
import com.zlc.community.model.User;
import com.zlc.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

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

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request){
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
        GithubUser githubUser = githubProvider.getUser(accessToken);
        // System.out.println(githubUser.getName());
        if (githubUser != null){
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId())); // String.valueOf:强转成String类型
            user.setGmtCreate(System.currentTimeMillis()); // 当前系统时间
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            // 登录成功，写cookie和session
            request.getSession().setAttribute("githubUser",githubUser); // html中可以${session.githubUser}取出数据
            return "redirect:/";
        }else {
            // 登录失败，重新登录
            return "redirect:/";
        }



    }

}
