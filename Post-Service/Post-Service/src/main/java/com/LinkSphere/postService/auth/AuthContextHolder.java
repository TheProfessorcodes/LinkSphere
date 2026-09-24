package com.LinkSphere.postService.auth;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

public class AuthContextHolder {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static Long getCurrentUserId(){
        return currentUserId.get();
    }

    static void setCurrentUserId(Long userId){
        currentUserId.set(userId);
    }

    static void clear(){
        currentUserId.remove();
    }
}
