package org.nutz.mvc.param.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mvc.param.ParamInjector;

public class NameInjector implements ParamInjector {

	protected String name;
	protected Class<?> type;

	public NameInjector(String name, Class<?> type) {
		if (null == name)
			throw Lang.makeThrow("Can not accept null as name, type '%s'", type.getName());
		this.name = name;
		this.type = type;
	}

	/**
	 * @Param refer 这个参考字段，如果有值，表示是路径参数的值，那么它比 request 里的参数优先
	 */
	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		String value;
		if (null != refer)
			value = refer.toString();
		else
			value = request.getParameter(name);
		return Castors.me().castTo(value, type);
	}

}
