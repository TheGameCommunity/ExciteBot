package com.gamebuster19901.excite.exception;

@SuppressWarnings("serial")
public class WiimmfiResponseException extends Exception {

	public WiimmfiResponseException(String msg) {
		super(msg);
	}
	
	public WiimmfiResponseException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public WiimmfiResponseException(Throwable cause) {
		super(cause);
	}
	
}
