package com.zzh.json;

@SuppressWarnings("serial")
public class JsonException extends RuntimeException {

	public JsonException(Throwable cause) {
		super(cause);
	}

	public JsonException(int row, int col, char cursor, String message) {
		super(String.format("!Json syntax error nearby [row:%d,col:%d char '%c'], reason: '%s'",
				row, col, cursor, message));
	}

}
