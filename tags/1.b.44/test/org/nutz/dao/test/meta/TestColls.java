package org.nutz.dao.test.meta;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_wire")
public class TestColls {

    @Id
    private Long id;
    @Column
    @ColDefine(type=ColType.VARCHAR,width=1024)
    private Map<Integer, String> map;

    @Column
    @ColDefine(type=ColType.VARCHAR,width=1024)
    private List<String> list;

    @Column("f_set")
    @ColDefine(type=ColType.VARCHAR,width=1024)
    private Set<String> set;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Map<Integer, String> getMap() {
		return map;
	}
	public void setMap(Map<Integer, String> map) {
		this.map = map;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public Set<String> getSet() {
		return set;
	}
	public void setSet(Set<String> set) {
		this.set = set;
	}
    
    
}