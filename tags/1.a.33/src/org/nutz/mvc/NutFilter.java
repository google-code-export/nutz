package org.nutz.mvc;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.init.Inits;
import org.nutz.mvc.init.config.FilterNutConfig;

/**
 * 同 JSP/Serlvet 容器的挂接点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutFilter implements Filter {

	private static final String IGNORE = "^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf)$";

	private static final Log log = Logs.getLog(NutFilter.class);

	private UrlMap urls;

	private FilterNutConfig config;

	private Pattern ignorePtn;

	public void init(FilterConfig conf) throws ServletException {
		config = new FilterNutConfig(conf);
		// 如果仅仅是用来更新 Message 字符串的，不加载 Nutz.Mvc 设定
		// @see Issue 301
		String skipMode = Strings.sNull(conf.getInitParameter("skip-mode"), "false").toLowerCase();
		if (!"true".equals(skipMode)) {
			Loading ing = Inits.init(config, true);
			if (null != ing)
				urls = ing.getUrls();

			String regx = Strings.sNull(config.getInitParameter("ignore"), IGNORE);
			if (!"null".equalsIgnoreCase(regx)) {
				ignorePtn = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
			}
		}
	}

	public void destroy() {
		if (null != urls)
			Inits.destroy(config);
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		// 更新 Request 必要的属性
		Mvcs.updateRequestAttributes((HttpServletRequest) req);

		if (null != urls) {
			RequestPath path = Mvcs.getRequestPathObject((HttpServletRequest) req);
			if (null == ignorePtn || !ignorePtn.matcher(path.getUrl()).find()) {
				ActionInvoking ing = urls.get(path.getPath());
				if (null != ing && null != ing.getInvoker()) {
					if (log.isInfoEnabled())
						log.info(path);
					ing.invoke(	config.getServletContext(),
								(HttpServletRequest) req,
								(HttpServletResponse) resp);
					return;
				}
			}
		}

		// 本过滤器没有找到入口函数，继续其他的过滤器
		chain.doFilter(req, resp);
	}
}