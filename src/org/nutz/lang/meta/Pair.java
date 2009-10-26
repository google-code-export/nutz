package org.nutz.lang.meta;

import org.nutz.lang.Strings;

public class Pair {

	public Pair() {}

	private static final String PTN_1 = "%s=%s";
	private static final String PTN_2 = "%s='%s'";
	private static final String PTN_3 = "%s=\"%s\"";

	public Pair(String name, String value) {
		this.name = name;
		this.value = value;
		pattern = PTN_3;
	}

	public Pair(String s) {
		String[] ss = Strings.splitIgnoreBlank(s, "=");
		if (null != ss)
			if (ss.length == 1) {
				name = ss[0];
			} else if (ss.length == 2) {
				name = ss[0];
				if (ss[1].length() > 0) {
					if (ss[1].charAt(0) == '"') {
						value = ss[1].substring(1, ss[1].length() - 2);
						pattern = PTN_3;
					} else if (ss[1].charAt(0) == '\'') {
						value = ss[1].substring(1, ss[1].length() - 2);
						pattern = PTN_2;
					} else {
						value = ss[1];
						pattern = PTN_1;
					}
				}
			}
	}

	private String name;

	private String value;

	private String pattern;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Pair)
			return Strings.equals(((Pair) obj).name, name)
					&& Strings.equals(((Pair) obj).value, value);
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		String v = null == value ? "" : value;
		v = v.replace("\"", "&quot;");
		return String.format(pattern, name, v);
	}

}
