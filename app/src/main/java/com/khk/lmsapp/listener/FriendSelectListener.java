package com.khk.lmsapp.listener;

import com.khk.lmsapp.modules.Teachers;

import java.util.List;

public interface FriendSelectListener {
    void onFriendSelectAdd(Teachers selectedFriend);
    void onFriendSelectRemove(Teachers selectedFriend);
}
