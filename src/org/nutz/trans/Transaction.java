package org.nutz.trans;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class Transaction {

	private int level;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public abstract int getId();

	protected abstract void commit() throws SQLException;

	protected abstract void rollback();

	public abstract Connection getConnection(DataSource dataSource) throws SQLException;

	public abstract void resetTransactionLevel() throws SQLException;

}
