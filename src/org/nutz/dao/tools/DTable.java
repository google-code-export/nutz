package org.nutz.dao.tools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DTable {

	private String name;

	private List<DField> fields;

	private Map<String, DField> maps;

	public DTable() {
		fields = new LinkedList<DField>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DTable addField(DField field) {
		fields.add(field);
		return this;
	}

	public List<DField> getFields() {
		return fields;
	}

	public DField getField(String name) {
		if (null == maps) {
			synchronized (this) {
				if (null == maps) {
					maps = new HashMap<String, DField>();
					for (DField df : fields)
						maps.put(df.getName(), df);
				}
			}
		}
		return maps.get(name);
	}

	public boolean hasField(String name) {
		return null != getField(name);
	}

}
