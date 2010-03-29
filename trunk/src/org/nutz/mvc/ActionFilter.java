package org.nutz.mvc;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

/**
 * 入口函数的过滤器，你的过滤器实现只需要实现一个函数 match。 这个函数如果你返回的是 null，表示你的过滤器认为，可以继续。 如果你的函数返回一个
 * View 对象，就表示你的过滤器认为这个请求有问题，不能继续进行下一步操作。 直接用返回的 View 渲染 response 即可。
 * <p>
 * 你可以通过 '@Filters' 以及 '@By' 为任何一个入口函数，或者模块声明你的过滤器。
 * <p>
 * 你的过滤去，的构造函数的参数，要和你的 '@By' 的参数相匹配
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ActionFilter {

	/**
	 * 执行过滤
	 * 
	 * @param request
	 *            当前的请求对象
	 * @param method
	 *            即将调用的入口函数
	 * @return <ul>
	 *         <li>null - 过滤去通过，可以继续执行后续操作
	 *         <li>View 对象实例 - 过滤器认为应该终止操作，用这个视图对象来直接渲染 HTTP响应
	 *         </ul>
	 */
	View match(HttpServletRequest request, Method method);

}
