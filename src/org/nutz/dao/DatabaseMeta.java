package org.nutz.dao;

/**
 * 数据库的元数据
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DatabaseMeta {

	public DatabaseMeta() {
		type = DB.OTHER;
	}

	/**
	 * 现在所支持的数据库类型
	 */
	private DB type;

	/**
	 * 产品版本号
	 */
	private String version;

	/**
	 * 产品名称
	 */
	private String productName;

	public String getProductName() {
		return productName;
	}

	public String toString() {
		return String.format("%s:[%s - %s]", type.name(), productName, version);
	}

	public void setProductName(String productName) {
		this.productName = productName;
		String proName = productName.toLowerCase();
		if (proName.startsWith("postgresql")) {
			this.type = DB.PSQL;
		} else if (proName.startsWith("mysql")) {
			this.type = DB.MYSQL;
		} else if (proName.startsWith("oracle")) {
			this.type = DB.ORACLE;
		} else if (proName.startsWith("db2")) {
			this.type = DB.DB2;
		} else if (proName.startsWith("microsoft sql")) {
			this.type = DB.SQLSERVER;
		} else {
			this.type = DB.OTHER;
		}
	}

	public String getResultSetMetaSql(String tableName) {
		if (this.isMySql() || this.isPostgresql()) {
			return "SELECT * FROM " + tableName + " LIMIT 1";
		} else if (this.isSqlServer()) {
			return "SELECT TOP 1 * FROM " + tableName;
		}
		return "SELECT * FROM " + tableName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setAsMysql() {
		this.type = DB.MYSQL;
	}

	public void setAsPsql() {
		this.type = DB.PSQL;
	}

	public void setAsOracle() {
		this.type = DB.ORACLE;
	}

	public void setAsSqlServer() {
		this.type = DB.SQLSERVER;
	}

	public void setAsDB2() {
		this.type = DB.DB2;
	}

	public void setAsOther() {
		this.type = DB.OTHER;
	}

	public boolean is(String typeName) {
		return type.name().equalsIgnoreCase(typeName);
	}

	public DB getType() {
		return type;
	}

	public String getTypeName() {
		return type.name();
	}

	public boolean isOther() {
		return DB.OTHER == type;
	}

	public boolean isMySql() {
		return DB.MYSQL == type;
	}

	public boolean isPostgresql() {
		return DB.PSQL == type;
	}

	public boolean isSqlServer() {
		return DB.SQLSERVER == type;
	}

	public boolean isOracle() {
		return DB.ORACLE == type;
	}

	public boolean isDB2() {
		return DB.DB2 == type;
	}

}
