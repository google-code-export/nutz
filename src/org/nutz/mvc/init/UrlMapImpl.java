package org.nutz.mvc.init;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.invoker.ActionInvokerImpl;

public class UrlMapImpl implements UrlMap {

	private Ioc ioc;

	private PathNode<ActionInvoker> root;

	public UrlMapImpl(Ioc ioc) {
		this.ioc = ioc;
		root = new PathNode<ActionInvoker>();
	}

	private Ok ok;
	private Fail fail;
	private AdaptBy adaptBy;
	private Filters filters;
	private Encoding encoding;

	void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	void setOk(Ok ok) {
		this.ok = ok;
	}

	void setFail(Fail fail) {
		this.fail = fail;
	}

	void setAdaptBy(AdaptBy adaptBy) {
		this.adaptBy = adaptBy;
	}

	void setFilters(Filters filters) {
		this.filters = filters;
	}

	public void add(List<ViewMaker> makers, Class<?> module) {
		// create object
		InjectName name = module.getAnnotation(InjectName.class);
		Object obj;
		if (null == name) {
			try {
				obj = module.newInstance();
			} catch (Exception e) {
				throw Lang.makeThrow(
						"Class '%s' should has a accessible default constructor : '%s'", module
								.getName(), e.getMessage());
			}
		} else {
			obj = ioc.get(module, name.value());
		}
		// View: OK
		Ok myOk = module.getAnnotation(Ok.class);
		if (null == myOk)
			myOk = ok;
		// View: Defeat
		Fail myFail = module.getAnnotation(Fail.class);
		if (null == myFail)
			myFail = fail;
		// get default HttpAdaptor
		AdaptBy myAb = module.getAnnotation(AdaptBy.class);
		if (null == myAb)
			myAb = adaptBy;
		// get default ActionFilter
		Filters myFlts = module.getAnnotation(Filters.class);
		if (null == myFlts)
			myFlts = filters;
		// get encoding
		Encoding myEncoding = module.getAnnotation(Encoding.class);
		if (null == myEncoding)
			myEncoding = encoding;

		// get base url
		At baseUrl = module.getAnnotation(At.class);
		String basePath;
		if (null == baseUrl)
			basePath = "";
		else if (Strings.isBlank(baseUrl.value()))
			basePath = "/" + module.getSimpleName().toLowerCase();
		else
			basePath = baseUrl.value();
		// looping methods
		for (Method method : module.getMethods()) {
			// get Url
			At url = method.getAnnotation(At.class);
			if (null == url)
				continue;
			String path;
			if (Strings.isBlank(url.value()))
				path = basePath + "/" + method.getName().toLowerCase();
			else
				path = basePath + url.value();
			root.add(path, new ActionInvokerImpl(ioc, makers, obj, method, myOk, myFail, myAb,
					myFlts, myEncoding));
		}
	}

	public ActionInvoker get(String path) {
		return root.get(path);
	}

}
