package com.mec.chat_room.server.user.service;

import com.mec.chat_room.server.user.dao.UserDao;
import com.mec.chat_room.server.user.model.UserInfo;

public class UserService {
	private UserDao userDao;
	
	public UserService() {
		this.userDao = new UserDao();
	}

	public UserInfo getUserById(String id, String password) {
		UserInfo user = userDao.getUser(id);
		
		if (user == null || !password.equals(user.getPassword())) {
			return null;
		}
		
		return user;
	}
	
}
