package org.nutz.dao.pager;

import org.nutz.dao.DatabaseMeta;

/**
 * 默认的 Pager 工厂类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DefaultPagerMaker implements PagerMaker {

	public Pager make(DatabaseMeta meta, int pageNumber, int pageSize) {
		if (pageNumber < 1 || pageSize <= 0)
			return null;

		Pager pager;
		// MySql & H2
		if (meta.isMySql() || meta.isH2()) {
			pager = new MysqlPager();
		}
		// Postgresql
		else if (meta.isPostgresql()) {
			pager = new PostgresqlPager();
		}
		// Oracle
		else if (meta.isOracle()) {
			pager = new OraclePager();
		}
		// SqlServer
		else if (meta.isSqlServer()) {
			// SqlServer 2000: version like "8.00.2039"
			if (meta.getVersion().contains("8.00"))
				pager = new SqlServer2000Pager();
			else
				pager = new SqlServer2005Pager();
		}
		// DB2
		else if (meta.isDB2()) {
			pager = new DB2Pager();
		}
		// Other
		else {
			pager = new OtherPager();
		}
		pager.setPageNumber(pageNumber);
		pager.setPageSize(pageSize);
		return pager;
	}

}
