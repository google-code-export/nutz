package org.nutz.dao.impl.jdbc.sqlserver2005;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;

/**
 * 
 * @author wendal
 */
public class Sqlserver2005JdbcExpert extends AbstractJdbcExpert {

	public Sqlserver2005JdbcExpert(JdbcExpertConfigFile conf) {
		super(conf);
	}

	public String getDatabaseType() {
		return DB.SQLSERVER.name();
	}

	public boolean createEntity(Dao dao, Entity<?> en) {
		StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
		// 创建字段
		for (MappingField mf : en.getMappingFields()) {
			sb.append('\n').append(mf.getColumnName());
			sb.append(' ').append(evalFieldType(mf));
			// 非主键的 @Name，应该加入唯一性约束
			if (mf.isName() && en.getPkType() != PkType.NAME) {
				sb.append(" UNIQUE NOT NULL");
			}
			// 普通字段
			else {
				if (mf.isUnsigned())
					sb.append(" UNSIGNED");
				if (mf.isNotNull())
					sb.append(" NOT NULL");
				if (mf.isAutoIncreasement())
					sb.append(" IDENTITY");
				if (mf.hasDefaultValue())
					sb.append(" DEFAULT '").append(getDefaultValue(mf)).append('\'');
			}
			sb.append(',');
		}
		// 创建主键
		List<MappingField> pks = en.getPks();
		if (!pks.isEmpty()) {
			sb.append('\n');
			sb.append("PRIMARY KEY (");
			for (MappingField pk : pks) {
				sb.append(pk.getColumnName()).append(',');
			}
			sb.setCharAt(sb.length() - 1, ')');
			sb.append("\n ");
		}
		// 创建索引
		// TODO ...

		// 结束表字段设置
		sb.setCharAt(sb.length() - 1, ')');

		// 执行创建语句
		dao.execute(Sqls.create(sb.toString()));
		// 创建关联表
		createRelation(dao, en);

		return true;
	}

	@Override
	protected String evalFieldType(MappingField mf) {
		switch (mf.getColumnType()) {
		case BOOLEAN:
			return "BIT";

		case TIMESTAMP:
			return "DATETIME";// TODO 值得讨论

		case DATETIME:
		case DATE:
		case TIME:
			return "DATETIME";
		case INT:
			// 用户自定义了宽度
			if (mf.getWidth() > 0)
				return "NUMERIC(" + mf.getWidth() + ")";
			// 用数据库的默认宽度
			return "INT";

		case FLOAT:
			// 用户自定义了精度
			if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
				return "decimal(" + mf.getWidth() + "," + mf.getPrecision() + ")";
			}
			// 用默认精度
			if (mf.getTypeMirror().isDouble())
				return "decimal(15,10)";
			return "float";
		}
		return super.evalFieldType(mf);
	}

	public void formatQuery(Pojo pojo) {
		Pager pager = pojo.getContext().getPager();
		if (null != pager && pager.getPageNumber() > 0) {
			// -----------------------------------------------------
			// TODO XXX 这个写法灰常暴力!!But , it works!!!! 期待更好的写法
			PItem pi = pojo.getItem(0);
			StringBuilder sb = new StringBuilder();
			pi.joinSql(pojo.getEntity(), sb);
			String str = sb.toString();
			if (str.trim().toLowerCase().startsWith("select")) {
				pojo.setItem(0, Pojos.Items.wrap(str.substring(6)));
			} else
				return;// 以免出错.
			pojo.insertFirst(Pojos.Items.wrapf(	"select * from(select row_number()over(order by __tc__)__rn__,* from(select top %d 0 __tc__, ",
												pager.getOffset() + pager.getPageSize()));
			pojo.append(Pojos.Items.wrapf(")t)tt where __rn__ > %d", pager.getOffset()));
		}
	}

	@Override
	protected String createResultSetMetaSql(Entity<?> en) {
		return "SELECT top 1 * FROM " + en.getViewName();
	}
}
