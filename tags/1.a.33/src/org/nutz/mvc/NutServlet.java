package org.nutz.mvc;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.Nutz;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.init.Inits;
import org.nutz.mvc.init.config.ServletNutConfig;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	private static final Log log = Logs.getLog(NutServlet.class);

	/**
	 * Nutz.Mvc 的参数映射表
	 */
	private UrlMap urls;

	private ServletNutConfig config;

	/**
	 * Nutz.Mvc 是否成功的被挂接在 JSP/Servlet 容器上。这个标志位可以为子类提供参考
	 */
	private boolean ok;

	protected boolean isOk() {
		return ok;
	}

	@Override
	public void init() throws ServletException {
		if (log.isInfoEnabled()) {
			URL me = Thread.currentThread()
							.getContextClassLoader()
							.getResource(NutServlet.class.getName().replace('.', '/') + ".class");
			log.infof("Nutz Version : %s in %s", Nutz.version(), me);
		}
		config = new ServletNutConfig(getServletConfig());
		Loading ing = Inits.init(config, false);
		urls = ing.getUrls();
		ok = true;
	}

	public void destroy() {
		if (config.getMainModule() != null)
			Inits.destroy(config);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (null == urls) {
			if (log.isErrorEnabled())
				log.error("!!!This servlet is destroyed!!! Noting to do!!!");
			return;
		}

		Mvcs.updateRequestAttributes(req);
		String path = Mvcs.getRequestPath(req);

		if (log.isInfoEnabled())
			log.info("HttpServletRequest path = " + path);

		// get Url and invoke it
		ActionInvoking ing = urls.get(path);
		if (null == ing || null == ing.getInvoker())
			resp.setStatus(404);
		else
			ing.invoke(config.getServletContext(), req, resp);
	}
}