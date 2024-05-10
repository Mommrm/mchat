package com.mtalk.utils;

import com.mtalk.entity.User;

public class LocalUser {
    private static final ThreadLocal<User> localUser = new ThreadLocal<>();

    public static void setLocalUser(User user){
        localUser.set(user);
    }

    public static User getLocalUser(){
        return localUser.get();
    }

    public static void removeLocalUser(){
        localUser.remove();
    }
}
