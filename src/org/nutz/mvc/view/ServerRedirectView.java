package org.nutz.mvc.view;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.mvc.View;

/**
 * 重定向视图
 * <p>
 * 在入口函数上声明：
 * <p>
 * '@Ok("redirect:/pet/list.nut")'
 * <p>
 * 实际上相当于：<br>
 * new ServerRedirectView("/pet/list.nut");
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ServerRedirectView implements View {

	private Segment dest;

	public ServerRedirectView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		Mirror<?> mirror = Mirror.me(obj);

		// Fill path
		Set<String> keySet = dest.keys();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = null;
			int length = key.length();
			if (null != mirror && key.startsWith("obj.") && length > 4) {
				value = mirror.getValue(obj, key.substring(4));
			} else if (null != mirror && key.startsWith("p.") && length > 2) {
				value = req.getParameter(key.substring(2));
			} else {
				value = obj;
			}
			if (null == value)
				value = obj;
			dest.set(key, value);
		}

		// Format the path ...
		String path = dest.toString();
		// Absolute path, add the context path for it
		if (path.startsWith("/")) {
			path = req.getContextPath() + path;
		}
		// Relative path, add current URL path for it
		else {
			String myPath = req.getPathInfo();
			int pos = myPath.lastIndexOf('/');
			if (pos > 0)
				path = myPath.substring(0, pos) + "/" + path;
			else
				path = "/" + path;
		}
		resp.sendRedirect(path);
		resp.flushBuffer();
	}

}
