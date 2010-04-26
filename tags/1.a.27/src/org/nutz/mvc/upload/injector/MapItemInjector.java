package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.injector.NameInjector;

public class MapItemInjector extends NameInjector {

	public MapItemInjector(String name, Class<?> type) {
		super(name, type);
	}

	@Override
	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		if (null != refer)
			if (refer instanceof Map<?, ?>) {
				Object value = ((Map<?, ?>) refer).get(name);
				return Castors.me().castTo(value, type);
			}
		return null;
	}

}
