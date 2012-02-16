package org.nutz.dao.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;

public class ViewTest extends DaoCase {

	
	@Test
	public void test_insert() throws Throwable {
		dao.create(User.class, true);
		final List<User> us = new ArrayList<User>();
		for (int i = 0; i < 100; i++) {
			User u = new User();
			u.setName("XXXX" + i);
			us.add(u);
		}
		try {
			Trans.exec(new Molecule<Object>() {
				@Override
				public void run() {
					dao.fastInsert(us);
					throw new RuntimeException();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		assertEquals(0, dao.count(User.class));
	}
}
