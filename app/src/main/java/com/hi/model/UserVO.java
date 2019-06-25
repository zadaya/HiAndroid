package com.hi.model;

import java.util.List;

public class UserVO {

    private UserServer myselfInfo;

    private List<UserServer> friends;

    public UserServer getMyselfInfo() {
        return myselfInfo;
    }

    public void setMyselfInfo(UserServer myselfInfo) {
        this.myselfInfo = myselfInfo;
    }

    public List<UserServer> getFriends() {
        return friends;
    }

    public void setFriends(List<UserServer> friends) {
        this.friends = friends;
    }
}