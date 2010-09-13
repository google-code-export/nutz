package org.nutz.mvc.init;

import org.nutz.ioc.Ioc;
import org.nutz.resource.ResourceScan;

/**
 * 这个接口是一个抽象封装
 * <p>
 * 如果是通过 Servlet 方式加载的 Nutz.Mvc， 只需要根据 ServletConfig 来实现一下这个接口 同理， Filter
 * 方式，甚至不是标准的 JSP/Servlet 容器，只要实现了这个接口，都可以 正常的调用 Loading 接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface NutConfig {

	/**
	 * @return 当前应用的 IOC 容器实例
	 */
	Ioc getIoc();

	/**
	 * @return 当前应用的根路径
	 */
	String getAppRoot();

	/**
	 * @return 当前应用的名称
	 */
	String getAppName();

	/**
	 * 获取配置的参数
	 * 
	 * @param name
	 *            参数名
	 * @return 参数值
	 */
	String getInitParameter(String name);

	/**
	 * 获取上下文环境中的属性对象
	 * 
	 * @param name
	 *            - 属性名
	 * 
	 * @return 值
	 */
	Object getAttribute(String name);

	/**
	 * 获取上下文环境中的属性对象，并自动转成指定类型
	 * 
	 * @param <T>
	 *            类型
	 * @param type
	 *            类型
	 * @param name
	 *            属性名
	 * @return 值
	 */
	<T> T getAttributeAs(Class<T> type, String name);

	/**
	 * 在上下文环境中设置属性对象
	 * 
	 * @param name
	 *            属性名
	 * @param obj
	 *            属性值
	 */
	void setAttribute(String name, Object obj);

	/**
	 * 在上下文环境中设置属性对象，如果值为 null，则忽略
	 * 
	 * @param name
	 *            属性名
	 * @param obj
	 *            属性值
	 */
	void setAttributeIgnoreNull(String name, Object obj);

	/**
	 * 获取配置的主模块，一般的说是存放在 initParameter 集合下的 "modules" 属性 值为一个 class 的全名
	 * 
	 * @return 配置的主模块，null - 如果没有定义这个参数
	 */
	Class<?> getMainModule();

	/**
	 * 根据当前的服务器配置，得到一个资源扫描接口
	 * 
	 * @return 资源扫描器
	 */
	ResourceScan scan();
}
