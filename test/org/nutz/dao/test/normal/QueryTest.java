package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;

public class QueryTest extends DaoCase {

	public void before() {
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/meta/pet.dod"));
		// Insert 8 records
		for (int i = 0; i < 8; i++)
			dao.insert(Pet.create("pet" + i));
	}

	@Test
	public void query_by_pager() {
		List<Pet> pets = dao.query(Pet.class, Cnd.orderBy().asc("name"), dao.createPager(3, 2));
		assertEquals(2, pets.size());
		assertEquals("pet4", pets.get(0).getName());
		assertEquals("pet5", pets.get(1).getName());
	}

	@Test
	public void fetch_by_name() {
		Pet pet = dao.fetch(Pet.class, Cnd.where("name", "=", "pet2"));
		assertEquals("pet2", pet.getName());
	}

	@Test
	public void query_records() {
		List<Record> pets = dao.query("t_pet", Cnd.orderBy().asc("name"), null);
		assertEquals(8, pets.size());
		assertEquals("pet0", pets.get(0).get("name"));
		assertEquals("pet7", pets.get(7).get("name"));
	}

	@Test
	public void query_records_pager() {
		List<Record> pets = dao.query("t_pet:id", Cnd.orderBy().asc("name"), dao.createPager(3, 2));
		assertEquals(2, pets.size());
		assertEquals("pet4", pets.get(0).get("name"));
		assertEquals("pet5", pets.get(1).get("name"));
	}

	@Test
	public void fetch_record() {
		Record re = dao.fetch("t_pet", Cnd.where("name", "=", "pet3"));
		Pet pet = re.toPojo(Pet.class);
		assertEquals(4, re.getColumnCount());
		assertEquals(4, pet.getId());
		assertEquals("pet3", pet.getName());
	}
}
