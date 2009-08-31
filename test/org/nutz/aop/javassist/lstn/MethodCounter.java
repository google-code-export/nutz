package org.nutz.aop.javassist.lstn;

import java.lang.reflect.Method;

import org.nutz.aop.MethodListener;

public class MethodCounter implements MethodListener {

	private int[] cc;

	public MethodCounter(int[] cc) {
		this.cc = cc;
	}

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		cc[1] += 1;
		return returnObj;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		cc[0] += 1;
		return true;
	}

	public void whenError(Throwable e, Object obj, Method method, Object... args) {
		cc[2] += 1;
	}

	public void whenException(Exception e, Object obj, Method method, Object... args) {
		cc[3] += 1;
	}

}
