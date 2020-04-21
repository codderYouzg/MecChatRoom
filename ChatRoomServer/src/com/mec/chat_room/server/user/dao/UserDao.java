package com.mec.chat_room.server.user.dao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mec.chat_room.server.user.model.UserInfo;
import com.mec.util.XMLParser;

public class UserDao {
	private Document document;

	public UserDao() {
		this.document = XMLParser.getDocument("/student.xml");
	}

	public UserInfo getUser(String id) {
		UserInfo user = new UserInfo();
		new XMLParser() {
			@Override
			public void dealElement(Element element, int index) {
				String strId = element.getAttribute("id");
				if (!strId.equals(id)) {
					return;
				}

				String nick = element.getAttribute("nick");
				String password = element.getAttribute("password");
				user.setId(strId);
				user.setNick(nick);
				user.setPassword(password);
			}
		}.parseTag(this.document, "student");
		
		if (user.getId() == null) {
			return null;
		}
		
		return user;
	}
	
}
