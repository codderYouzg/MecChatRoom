package com.mec.chat_room.server.user.action;

import com.mec.chat_room.server.user.model.UserInfo;
import com.mec.chat_room.server.user.service.UserService;
import com.mec.csframework.action.Actioner;
import com.mec.csframework.action.Argument;
import com.mec.csframework.action.Mapping;

@Actioner
public class UserAction {
	private UserService userService;

	public UserAction() {
		this.userService = new UserService();
	}
	
	@Mapping("userLogin")
	public UserInfo getUserById(
			@Argument("id") String id,
			@Argument("password") String password) {
		UserInfo user = userService.getUserById(id, password);
		
		if (user == null) {
			user = new UserInfo();
			user.setId("ERROR");
		} else {
			user.setPassword(null);
		}
		
		return user;
	}

}
