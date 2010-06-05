package org.nutz.aop;


public class DefaultClassDefiner extends ClassLoader implements ClassDefiner {
	
	public DefaultClassDefiner(ClassLoader parent) {
		super(parent);
	}

	public Class<?> define(String className, byte[] bytes) throws ClassFormatError {
		try {
			return load(className);
		}
		catch (ClassNotFoundException e) {}
		// If not found ...
		return defineClass(className, bytes, 0, bytes.length);
	}

	public boolean has(String className) {
		try {
			load(className);
			return true;
		}
		catch (ClassNotFoundException e) {}
		return false;
	}

	public Class<?> load(String className) throws ClassNotFoundException {
		try{
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException e) {
			try{
				return ClassLoader.getSystemClassLoader().loadClass(className);
			}catch (ClassNotFoundException e2) {
				try{
					return getParent().loadClass(className);
				}catch (ClassNotFoundException e3) {
				}
			}
		}
		return super.loadClass(className);
	}
}
