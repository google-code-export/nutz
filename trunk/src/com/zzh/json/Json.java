package com.zzh.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.zzh.lang.Lang;

public class Json {

	public static Object fromJson(Reader reader) throws JsonException {
		return new JsonParsing(reader).parseFromJson(null);
	}

	public static <T> T fromJson(Class<T> type, Reader ins) throws JsonException {
		return new JsonParsing(ins).parseFromJson(type);
	}

	public static String toJson(Object obj) {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		toJson(w, obj);
		return sb.toString();
	}

	public static String toJson(Object obj, JsonFormat format) {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		toJson(w, obj, format);
		return sb.toString();
	}

	public static void toJson(Writer writer, Object obj) {
		toJson(writer, obj, JsonFormat.nice());
	}

	public static void toJson(Writer writer, Object obj, JsonFormat format) {
		try {
			(new JsonRendering(writer, format)).render(obj);
			writer.flush();
		} catch (IOException e) {
			throw Lang.wrapThrow(e, JsonException.class);
		}
	}

}
