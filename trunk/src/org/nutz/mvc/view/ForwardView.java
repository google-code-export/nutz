package org.nutz.mvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 内部重定向视图<p/>
 * 根据传入的视图名，决定视图的路径：
 * <ul>
 * <li>如果视图名以 '/' 开头， 则被认为是一个 全路径
 * <li>否则，将视图名中的 '.' 转换成 '/'，并加入前缀 "/WEB-INF/"
 * </ul>
 * 通过注解映射的例子：
 * <ul>
 * <li>'@Ok("forward:abc.cbc")' => /WEB-INF/abc/cbc
 * <li>'@Ok("forward:/abc/cbc")' => /abc/cbc
 * <li>'@Ok("forward:/abc/cbc.jsp")' => /abc/cbc.jsp
 * </ul>
 * 
 * @author mawm(ming300@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ForwardView implements View {
	
	protected String path;

	public ForwardView(String name) {
		if (!Strings.isBlank(name))
			path = normalizePath(name, getExt());
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		// Check path
		String thePath = path;
		if (Strings.isBlank(thePath)) {
			thePath = Mvcs.getRequestPath(req);
			thePath = "/WEB-INF/" + Files.renameSuffix(thePath, getExt());
		}
		RequestDispatcher rd = req.getRequestDispatcher(thePath);
		if (rd == null)
			throw Lang.makeThrow("Fail to find Forward '%s'", thePath);
		// Do rendering
		rd.forward(req, resp);
	}

	protected String getExt() {
		return "";
	}

	public static String normalizePath(String name, String ext) {
		name = name.replace('\\', '/');
		// For: @Ok("jsp:/abc/cbc") || @Ok("jsp:/abc/cbc.jsp")
		if (name.charAt(0) == '/') {
			if (name.toLowerCase().endsWith(ext))
				return name;
			else
				return name + ext;
		}
		// For: @Ok("jsp:abc.cbc")
		return "/WEB-INF/" + name.replace('.', '/') + ext;
	}

}