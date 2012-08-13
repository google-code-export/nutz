package org.nutz.dao;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("tb_pojo")
public class Pojo {
	
	@Id
	private int id;
	
	@Column
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private Pojo p2 ;
	
	public void setP2(Pojo p2) {
		this.p2 = p2;
	}
	
	public Pojo getP2() {
		return p2;
	}
}
