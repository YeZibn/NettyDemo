package com.yzb.nettyAdvance.agreement.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {

    private Map<String, String> userMap = new ConcurrentHashMap<>();

    {
        userMap.put("zhangsan", "123");
        userMap.put("lisi", "123");
        userMap.put("wangwu", "123");
    }

    @Override
    public boolean login(String username, String password) {
        return userMap.get(username) != null && userMap.get(username).equals(password);
    }
}
