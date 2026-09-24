package com.LinkSphere.userService.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId=AuthContextHolder.getCurrentUserId();
        if(userId!=null){
            requestTemplate.header("X-User-Id",userId.toString());
        }
    }
}
