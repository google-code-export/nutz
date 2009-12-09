package org.nutz.lang;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 字符串操作的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class Strings {

	/**
	 * 复制字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param num
	 *            数量
	 * @return 新字符串
	 */
	public static String dup(CharSequence cs, int num) {
		if (isEmpty(cs))
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++)
			sb.append(cs);
		return sb.toString();
	}

	/**
	 * 复制字符
	 * 
	 * @param cs
	 *            字符
	 * @param num
	 *            数量
	 * @return 新字符串
	 */
	public static String dup(char c, int num) {
		if (c == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++)
			sb.append(c);
		return sb.toString();
	}

	/**
	 * 将字符串首字母大写
	 * 
	 * @param s
	 *            字符串
	 * @return 首字母大写后的新字符串
	 */
	public static String capitalize(CharSequence s) {
		if (null == s)
			return null;
		if (s.length() == 0)
			return "";
		if (Character.isUpperCase(s.charAt(0)))
			return s.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(s.charAt(0))).append(s.subSequence(1, s.length()));
		return sb.toString();
	}

	/**
	 * 检查两个字符串的忽略大小写后是否相等.
	 * <p/>
	 * <b>当s1 == null && s2 == null, 本方法返回false<b/>
	 * 
	 * @param s1
	 *            字符串A
	 * @param s2
	 *            字符串B
	 * @return true 如果两个字符串忽略大小写后相等,且两个字符串均不为null
	 */
	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		return s1.equalsIgnoreCase(s2);
	}

	/**
	 * 检查两个字符串是否相等.
	 * <p/>
	 * <b>当s1 == null && s2 == null, 本方法返回false<b/>
	 * 
	 * @param s1
	 *            字符串A
	 * @param s2
	 *            字符串B
	 * @return true 如果两个字符串相等,且两个字符串均不为null
	 */
	public static boolean equals(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		return s1.equals(s2);
	}

	/**
	 * @param cs
	 *            字符串
	 * @return 是不是为空字符串
	 */
	public static boolean isEmpty(CharSequence cs) {
		if (null == cs)
			return true;
		return cs.length() == 0;
	}

	/**
	 * @param cs
	 *            字符串
	 * @return 是不是为空白字符串
	 */
	public static boolean isBlank(CharSequence cs) {
		if (null == cs)
			return true;
		for (int i = 0; i < cs.length(); i++) {
			char c = cs.charAt(i);
			if (c > 0x20 || c < 0)
				return false;
		}
		return true;
	}

	/**
	 * 去掉字符串前后空白
	 * 
	 * @param cs
	 *            字符串
	 * @return 新字符串
	 */
	public static String trim(CharSequence cs) {
		if (null == cs)
			return null;
		if (cs.length() == 0)
			return cs.toString();
		int l = 0;
		int last = cs.length() - 1;
		int r = last;
		for (; l < cs.length(); l++) {
			char c = cs.charAt(l);
			if (c > 0x20 || c < 0)
				break;
		}
		for (; r > 0; r--) {
			char c = cs.charAt(r);
			if (c > 0x20 || c < 0)
				break;
		}
		if (l > r)
			return "";
		else if (l == 0 && r == last)
			return cs.toString();

		if (cs instanceof String)
			return ((String) cs).substring(l, r + 1);
		return cs.subSequence(l, r + 1).toString();
	}

	/**
	 * 将字符串按半角逗号，拆分成数组，空元素将被忽略
	 * 
	 * @param s
	 *            字符串
	 * @return 字符串数组
	 */
	public static String[] splitIgnoreBlank(String s) {
		return Strings.splitIgnoreBlank(s, ",");
	}

	/**
	 * 根据一个正则式，将字符串拆分成数组，空元素将被忽略
	 * 
	 * @param s
	 *            字符串
	 * @param regex
	 *            正则式
	 * @return 字符串数组
	 */
	public static String[] splitIgnoreBlank(String s, String regex) {
		if (null == s)
			return null;
		String[] ss = s.split(regex);
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < ss.length; i++) {
			if (isBlank(ss[i]))
				continue;
			list.add(trim(ss[i]));
		}
		String[] re = new String[list.size()];
		list.toArray(re);
		return re;
	}

	/**
	 * 将一个整数转换成最小长度为某一固定数值的十进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String fillDigit(int d, int width) {
		return Strings.alignRight(String.valueOf(d), width, '0');
	}

	/**
	 * 将一个整数转换成最小长度为某一固定数值的十六进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String fillHex(int d, int width) {
		return Strings.alignRight(Integer.toHexString(d), width, '0');
	}

	/**
	 * 将一个整数转换成最小长度为某一固定数值的二进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String fillBinary(int d, int width) {
		return Strings.alignRight(Integer.toBinaryString(d), width, '0');
	}
	
	/**
	 * 将一个整数转换成固定长度的十进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String toDigit(int d, int width) {
		return Strings.cutRight(String.valueOf(d), width, '0');
	}

	/**
	 * 将一个整数转换成固定长度的十六进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String toHex(int d, int width) {
		return Strings.cutRight(Integer.toHexString(d), width, '0');
	}

	/**
	 * 将一个整数转换成固定长度的二进制形式字符串
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            宽度
	 * @return 新字符串
	 */
	public static String toBinary(int d, int width) {
		return Strings.cutRight(Integer.toBinaryString(d), width, '0');
	}

	/**
	 * 保证字符串为一固定长度。超过长度，切除，否则补字符。
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            长度
	 * @param c
	 *            补字符
	 * @return 修饰后的字符串
	 */
	public static String cutRight(String s, int width, char c) {
		if (s.length() == width)
			return s;
		if (s.length() < width)
			return Strings.dup(c, width - s.length()) + s;
		return s.substring(s.length()-width, s.length());
	}

	/**
	 * 在字符串左侧填充一定数量的特殊字符
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            字符数量
	 * @param c
	 *            字符
	 * @return 新字符串
	 */
	public static String alignRight(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(dup(c, width - cs.length()));
		sb.append(cs);
		return sb.toString();
	}

	/**
	 * 在字符串右侧填充一定数量的特殊字符
	 * 
	 * @param cs
	 *            字符串
	 * @param width
	 *            字符数量
	 * @param c
	 *            字符
	 * @return 新字符串
	 */
	public static String alignLeft(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(cs);
		sb.append(dup(c, width - cs.length()));
		return sb.toString();
	}

	/**
	 * @param cs
	 *            字符串
	 * @param lc
	 *            左字符
	 * @param rr
	 *            右字符
	 * @return 字符串是被左字符和右字符包裹 -- 忽略空白
	 */
	public static boolean isQuoteByIgnoreBlank(CharSequence cs, char lc, char rc) {
		if (null == cs)
			return false;
		if (cs.length() < 2)
			return false;
		// check left
		int l = 0;
		int last = cs.length() - 1;
		int r = last;
		for (; l < cs.length(); l++) {
			char c = cs.charAt(l);
			if (c > 0x20 || c < 0)
				break;
		}
		for (; r > 0; r--) {
			char c = cs.charAt(r);
			if (c > 0x20 || c < 0)
				break;
		}
		if (l >= r)
			return false;
		else if (cs.charAt(l) != lc)
			return false;
		else if (cs.charAt(r) != rc)
			return false;
		return true;
	}

	/**
	 * @param cs
	 *            字符串
	 * @param lc
	 *            左字符
	 * @param rc
	 *            右字符
	 * @return 字符串是被左字符和右字符包裹
	 */
	public static boolean isQuoteBy(CharSequence cs, char lc, char rc) {
		if (null == cs)
			return false;
		if (cs.length() < 2)
			return false;
		if (cs.charAt(0) != lc)
			return false;
		if (cs.charAt(cs.length() - 1) != rc)
			return false;
		return true;
	}

	/**
	 * 获得一个字符串集合中，最长串的长度
	 * 
	 * @param coll
	 *            字符串集合
	 * @return 最大长度
	 */
	public static int maxLength(Collection<? extends CharSequence> coll) {
		int re = 0;
		if (null != coll)
			for (CharSequence s : coll)
				if (null != s)
					re = Math.max(re, s.length());
		return re;
	}

	/**
	 * 获得一个字符串数组中，最长串的长度
	 * 
	 * @param array
	 *            字符串数组
	 * @return 最大长度
	 */
	public static <T extends CharSequence> int maxLength(T[] array) {
		int re = 0;
		if (null != array)
			for (CharSequence s : array)
				if (null != s)
					re = Math.max(re, s.length());
		return re;
	}
}
