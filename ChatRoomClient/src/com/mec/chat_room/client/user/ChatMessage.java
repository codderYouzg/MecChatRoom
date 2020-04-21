package com.mec.chat_room.client.user;

public class ChatMessage {
	private EAppCommand command;
	private String message;

	public ChatMessage() {
	}

	public EAppCommand getCommand() {
		return command;
	}

	public void setCommand(EAppCommand command) {
		this.command = command;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
