package com.zzh.dao.test.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zzh.dao.TableName;
import com.zzh.dao.test.DaoCase;
import com.zzh.dao.test.meta.Base;
import com.zzh.dao.test.meta.Platoon;
import com.zzh.dao.test.meta.Soldier;
import com.zzh.dao.test.meta.Tank;
import com.zzh.trans.Atom;

public class DynamicManyMany extends DaoCase {

	private Platoon platoon;

	@Override
	protected void before() {
		pojos.init();
		platoon = pojos.create4Platoon(Base.make("blue"), "seals");
	}

	@Override
	protected void after() {}

	@Test
	public void delete_links() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				assertEquals(3, t.getMembers().size());
				dao.deleteLinks(t, "members");
				assertEquals(2, dao.count(Soldier.class));
				assertEquals(2, dao.count("dao_d_m_soldier_tank_1"));
			}
		});
	}

	@Test
	public void delete_links_partly() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				t.getMembers().remove("Mick");
				dao.deleteLinks(t, "members");
				assertEquals(2, dao.count(Tank.class));
				assertEquals(3, dao.count(Soldier.class));
				assertEquals(3, dao.count("dao_d_m_soldier_tank_1"));
			}
		});
	}

	@Test
	public void delete_with() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				dao.deleteWith(t, "members");
				assertEquals(1, dao.count(Tank.class));
				assertEquals(2, dao.count(Soldier.class));
				assertEquals(2, dao.count("dao_d_m_soldier_tank_1"));
			}
		});
	}

	@Test
	public void delete_with_partly() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				t.getMembers().remove("Peter");
				dao.deleteWith(t, "members");
				assertEquals(1, dao.count(Tank.class));
				assertEquals(3, dao.count(Soldier.class));
				assertEquals(3, dao.count("dao_d_m_soldier_tank_1"));
			}
		});
	}

	@Test
	public void clear_links() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				dao.clearLinks(t, "members");
				assertEquals(5, dao.count(Soldier.class));
				assertEquals(3, dao.count("dao_d_m_soldier_tank_1"));
			}
		});
	}

	@Test
	public void update_links() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				t.setWeight(42);
				t.getMembers().get("ZZH").setAge(30);
				t.getMembers().get("Mick").setAge(22);
				t.getMembers().get("Peter").setAge(28);
				dao.updateLinks(t, "members");
				assertEquals(30, dao.fetch(Soldier.class, "ZZH").getAge());
				assertEquals(22, dao.fetch(Soldier.class, "Mick").getAge());
				assertEquals(28, dao.fetch(Soldier.class, "Peter").getAge());
				assertEquals(0, dao.fetch(Tank.class, "T92").getWeight());
			}
		});
	}

	@Test
	public void update_with() {
		TableName.run(platoon, new Atom() {
			public void run() {
				Tank t = dao.fetchLinks(dao.fetch(Tank.class, "T92"), "members");
				t.setWeight(42);
				t.getMembers().get("ZZH").setAge(30);
				t.getMembers().get("Mick").setAge(22);
				t.getMembers().get("Peter").setAge(28);
				dao.updateWith(t, "members");
				assertEquals(30, dao.fetch(Soldier.class, "ZZH").getAge());
				assertEquals(22, dao.fetch(Soldier.class, "Mick").getAge());
				assertEquals(28, dao.fetch(Soldier.class, "Peter").getAge());
				assertEquals(42, dao.fetch(Tank.class, "T92").getWeight());
			}
		});
	}
}
