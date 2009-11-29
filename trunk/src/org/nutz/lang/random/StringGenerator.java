package org.nutz.lang.random;

/**
 * @author zozohtnt
 * @author wendal(wendal1985@gmail.com)
 * @version $Revision$
 */
public class StringGenerator {

	/**
	 * 
	 * @param max 必须大于0
	 */
	public StringGenerator(int max) {
		maxLen = max;
		minLen = 1;
	}

	/**
	 * 
	 * @param min 必须大于0
	 * @param max 必须不小于min
	 */
	public StringGenerator(int min, int max) {
		maxLen = max;
		minLen = min;
	}

	/**
	 * min length of the string
	 */
	private int maxLen;

	/**
	 * max length of the string
	 */
	private int minLen;
	
	/**
	 * 
	 * @param min 必须大于0
	 * @param max 必须不小于min
	 */
	public void setup(int max, int min) {
		maxLen = max;
		minLen = min;
	}

	/**
	 * 根据设置的max和min的长度,生成随机字符串.
	 * <p/>若max或min小于0,则返回null
	 * @return 生成的字符串
	 */
	public String next() {
		if (maxLen <= 0 || minLen <= 0)
			return null;
		char[] buf = new char[GM.random(minLen, maxLen)];
		for (int i = 0; i < buf.length; i++)
			buf[i] = CharGenerator.next();
		return new String(buf);
	}

}
