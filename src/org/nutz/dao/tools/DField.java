package org.nutz.dao.tools;

import org.nutz.lang.Strings;

public class DField {

	private String name;
	private String type;
	private boolean autoIncreament;
	private boolean primaryKey;
	private boolean unique;
	private boolean notNull;
	private boolean unsign;
	private String defaultValue;

	public boolean isUnsign() {
		return unsign;
	}

	public void setUnsign(boolean unsign) {
		this.unsign = unsign;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAutoIncreament() {
		return autoIncreament;
	}

	public void setAutoIncreament(boolean autoIncreament) {
		this.autoIncreament = autoIncreament;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isUnique() {
		return unique || primaryKey;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isNotNull() {
		return notNull || primaryKey;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		sb.append(" ").append(type).append(" ");
		appendByFlag(sb, autoIncreament, "+");
		appendByFlag(sb, notNull, "!");
		appendByFlag(sb, unsign, "~");
		appendByFlag(sb, primaryKey, "PK");
		appendByFlag(sb, unique, "UNIQUE");
		appendByFlag(sb, !Strings.isBlank(defaultValue), String.format(" <%s>", defaultValue));
		return sb.toString();
	}

	private void appendByFlag(StringBuilder sb, boolean flag, String str) {
		if (flag)
			sb.append(str);
	}
}
