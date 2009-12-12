package org.nutz.log.impl;

/**
 * 默认的Log,输出到System.err.
 * 
 * @author Young(sunonfire@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class SystemLog extends AbstractLog{
	
	private boolean output = true;
	
	private static SystemLog systemLog = new SystemLog();
	
	public static SystemLog me(){
		return systemLog;
	}
	
	private SystemLog() {
	}
	
	public void needOutput(boolean flag) {
		output = flag;
	}
	
	public boolean isDebugEnabled() {
		return output && super.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return output && super.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return output && super.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return output && super.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return output && super.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return output && super.isWarnEnabled();
	}

	public void debug(Object message, Throwable t) {
		if(isDebugEnabled())
			printOut(message, t);
	}

	public void error(Object message, Throwable t) {
		if(isErrorEnabled())
			printOut(message, t);
	}

	public void fatal(Object message, Throwable t) {
		if(isFatalEnabled())
			printOut(message, t);
	}

	public void info(Object message, Throwable t) {
		if(isInfoEnabled())
			printOut(message, t);
	}

	public void trace(Object message, Throwable t) {
		if(isTraceEnabled())
			printOut(message, t);
	}

	public void warn(Object message, Throwable t) {
		if(isWarnEnabled())
			printOut(message, t);
	}
	
	private void printOut(Object message,Throwable t){
		System.err.println(message);
		if(t != null)
			t.printStackTrace(System.err);
	}

}
