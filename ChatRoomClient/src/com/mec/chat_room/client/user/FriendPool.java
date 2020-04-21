package com.mec.chat_room.client.user;

import java.util.HashMap;
import java.util.Map;

import com.mec.chat_room.client.model.UserInfo;

public class FriendPool {
	private Map<String, UserInfo> userPool;
	
	public FriendPool() {
		this.userPool = new HashMap<>();
	}

	public void addFriend(UserInfo user) {
		userPool.put(user.getId(), user);
	}
	
	public void removeFriend(UserInfo user) {
		userPool.remove(user.getId());
	}
	
	public UserInfo getUserById(String userId) {
		return userPool.get(userId);
	}
	
}
