package com.mec.chat_room.server.test;

import com.mec.chat_room.server.user.model.UserInfo;
import com.mec.chat_room.server.user.service.UserService;

public class Test {

	public static void main(String[] args) {
		UserService userService = new UserService();
		UserInfo user = userService.getUserById("20200002", "1450575459");
		System.out.println(user);
	}

}
