package com.mec.chat_room.client.Main;

import java.net.UnknownHostException;

import com.mec.chat_room.client.view.connect.ConnectToServerView;
import com.mec.csframework.action.ActionBeanFactory;
import com.mec.util.FrameIsNullException;

public class ChatRoomClientMain {

	public static void main(String[] args) {
		ActionBeanFactory.scanPackage("com.mec.chat_room.client.view");
		try {
			ConnectToServerView connectToServer = new ConnectToServerView();
			connectToServer.initClient();
			connectToServer.initView();
			connectToServer.showView();
			connectToServer.startConnect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (FrameIsNullException e) {
			e.printStackTrace();
		}
	}

}
