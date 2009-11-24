package org.nutz.dao;

import java.util.Iterator;

import org.nutz.lang.Lang;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;

/**
 * 将一个参考对象存入 ThreadLocal
 * <p>
 * Nutz.Dao 将在构造 SQL 是，参考这个对象。如何参考，请参看 '@Table' 关于 “动态表名的赋值规则” 的描述
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TableName {

	private static ThreadLocal<Object> object = new ThreadLocal<Object>();

	/**
	 * 代码模板，这个模板保证了，在 atom 中运行的 POJO 的动态表名，都会被参考对象所影响
	 * 
	 * @param refer
	 *            参考对象
	 * @param atom
	 *            你的业务逻辑
	 */
	public static void run(Object refer, Runnable atom) {
		Object old = get();
		set(refer);
		try {
			atom.run();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		} finally {
			set(old);
		}
	}

	/**
	 * @return 当前线程中的动态表名参考对象
	 */
	public static Object get() {
		return object.get();
	}

	/**
	 * 为当前线程设置动态表名参考对象
	 * 
	 * @param obj
	 *            参考对象
	 * @return 旧的动态表名参考对象
	 */
	public static Object set(Object obj) {
		Object re = get();
		object.set(obj);
		return re;
	}

	/**
	 * 清除当前线程的动态表名参考对象
	 */
	public static void clear() {
		set(null);
	}

	/**
	 * 根据当前线程的参考对象，渲染一个动态表名
	 * 
	 * @param tableName
	 *            动态表名
	 * @return 渲染后的表名
	 */
	public static String render(Segment tableName) {
		Object obj = get();
		if (null == obj)
			return tableName.toString();
		Segment seg = tableName.born();
		if (isPrimitive(obj)) {
			for (Iterator<String> it = seg.keys().iterator(); it.hasNext();) {
				seg.set(it.next(), obj);
			}
		} else {
			Segments.fillByKeys(seg, obj);
		}
		return seg.toString();
	}

	public static boolean isPrimitive(Object obj) {
		return obj instanceof CharSequence || obj instanceof Number || obj.getClass().isPrimitive();
	}
}
