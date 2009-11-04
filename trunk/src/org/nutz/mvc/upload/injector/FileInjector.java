package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.param.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public class FileInjector implements ParamInjector {

	public FileInjector(String name) {
		this.name = name;
	}

	private String name;

	@SuppressWarnings("unchecked")
	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		return ((TempFile) ((Map<String, Object>) refer).get(name)).getFile();
	}
}
