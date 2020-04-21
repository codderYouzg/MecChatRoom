package com.mec.chat_room.server.Main;

import com.mec.chat_room.server.view.ChatRoomServerView;
import com.mec.csframework.action.ActionBeanFactory;
import com.mec.util.FrameIsNullException;

public class ChatRoomServerMain {

	public static void main(String[] args) {
		try {
			ActionBeanFactory.scanPackage("com.mec.chat_room.server.user.action");
			ChatRoomServerView serverView = new ChatRoomServerView()
					.initServer();
			serverView.showView();
		} catch (FrameIsNullException e) {
			e.printStackTrace();
		}
	}

}
