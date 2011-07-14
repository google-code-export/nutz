package org.nutz.el.obj;

import org.nutz.el.ElCache;
import org.nutz.lang.util.Context;

/**
 * 标识符对象,即所有非数字,非字符串的对象.
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierObj {
	private String val;
	private ElCache ec;
	public IdentifierObj(String val) {
		this.val = val;
	}
	public String getVal() {
		return val;
	}
	public Object fetchVal(){
		Context context = ec.getContext();
		if(context != null && context.has(val)){
			return context.get(val);
		}
		return null;
	}
	public String toString() {
		return val;
	}
	public void setEc(ElCache ec) {
		this.ec = ec;
	}
}
