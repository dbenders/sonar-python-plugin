package com.popego.pylint;

public class Message {
	private String title;
	private String message;
	
	public Message(String title, String message) {
		this.title = title.trim();
		this.message = message.trim();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", title, message);
	}
}
