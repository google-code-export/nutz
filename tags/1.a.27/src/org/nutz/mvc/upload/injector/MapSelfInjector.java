package org.nutz.mvc.upload.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;

public class MapSelfInjector implements ParamInjector {

	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		return refer;
	}

}
