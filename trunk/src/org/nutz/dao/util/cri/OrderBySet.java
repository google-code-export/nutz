package org.nutz.dao.util.cri;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;

class OrderBySet extends NoParamsPItem implements OrderBy {

	private List<OrderByItem> list;

	OrderBySet() {
		list = new ArrayList<OrderByItem>(3);
	}

	public void joinSql(Entity<?> en, StringBuilder sb) {
		if (!list.isEmpty()) {
			sb.append(" ORDER BY ");
			for (OrderByItem obi : list) {
				obi.joinSql(en, sb);
				sb.append(", ");
			}
			sb.setCharAt(sb.length() - 2, ' ');
		}
	}

	public String toSql(Entity<?> entity) {
		return toString();
	}

	public OrderBy asc(String name) {
		OrderByItem asc = new OrderByItem(name, "ASC");
		asc.setPojo(pojo);
		list.add(asc);
		return this;
	}

	public OrderBy desc(String name) {
		OrderByItem desc = new OrderByItem(name, "DESC");
		desc.setPojo(pojo);
		list.add(desc);
		return this;
	}

	@Override
	public void setPojo(Pojo pojo) {
		super.setPojo(pojo);
		for (OrderByItem obi : list)
			obi.setPojo(pojo);
	}

}
