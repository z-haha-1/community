package com.zlc.community.provider;

import com.alibaba.fastjson.JSON;
import com.zlc.community.dto.AccessTokenDTO;
import com.zlc.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Provider:提供者
 * 用okhttp3发送post请求
 * accessTokenDTO类转换成json数据:用fastjson
 */
@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO)); // accessTokenDTO类转换成json数据
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string1 = response.body().string();

            String[] split = string1.split("&"); // 根据"&"拆分string1
            String tokenStr = split[0];
            String token = tokenStr.split("=")[1];

            System.out.println(token);
            System.out.println(string1);

            return token;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }


    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string(); // 得到的string是json格式的
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class); // JSON.parseObject:把string的json对象解析成GithubUser的类对象
            return githubUser;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;

    }

}
