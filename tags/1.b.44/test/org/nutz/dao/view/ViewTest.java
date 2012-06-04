package org.nutz.dao.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.TestColls;
import org.nutz.json.Json;

public class ViewTest extends DaoCase {

	
	@Test
	public void test_insert() throws Throwable {
		dao.create(TestColls.class, true);
		TestColls w = new TestColls();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "wendal");
		map.put(2, "zozoh");
		w.setMap(map);
		
		List<String> list = new ArrayList<String>();
		list.add("qq");
		list.add("google");
		w.setList(list);
		
		Set<String> set = new HashSet<String>();
		set.add("CCC");
		set.add("VVVV");
		w.setSet(set);
		
		dao.insert(w);
		Object obj = dao.fetch(TestColls.class, w.getId());
		System.out.println(Json.toJson(obj));
		
		HashMap<String,Object> map2 = new HashMap<String,Object>();
	    map2.put(".table", "t_wire");
//	    map2.put("+*id", 1);
	    map2.put("map", "{}");
	    dao.update(map2); 
	}
}
