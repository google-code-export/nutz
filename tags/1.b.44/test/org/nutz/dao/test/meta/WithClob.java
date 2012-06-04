package org.nutz.dao.test.meta;

import java.sql.Clob;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_clob")
public class WithClob {

	@Id
	private long id;
	
	@Name
	private String name;
	
	@Column("clob_me")
	@ColDefine(type=ColType.TEXT)
	private Clob clobMe;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClobMe(Clob clobMe) {
		this.clobMe = clobMe;
	}
	
	public Clob getClobMe() {
		return clobMe;
	}
}
