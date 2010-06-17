package org.nutz.lang;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.junit.Test;

import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;

public class LangTest {

	public static class A {
		private int id;
		private String name;
	}

	@Test
	public void test_map_2_object() throws FailToCastObjectException {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{id:23,name:'zzh'}"));
		A a = Lang.map2Object(map, A.class);
		assertEquals(23, a.id);
		assertEquals("zzh", a.name);
	}

	@Test
	public void test_map_2_map() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{id:23,name:'zzh'}"));
		Map<?, ?> map2 = Lang.map2Object(map, Map.class);
		assertTrue(Lang.equals(map, map2));
	}

	@Test
	public void test_char_reader() throws IOException {
		Reader r = Lang.inr("AB");
		assertEquals('A', (char) r.read());
		assertEquals('B', (char) r.read());
		assertEquals(-1, r.read());
	}

	@Test
	public void test_char_writer() throws IOException {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		w.write("AB");
		assertEquals("AB", sb.toString());
	}

	@Test
	public void test_map_equles() {
		Map<?, ?> map1 = Lang.map("{a:1,b:2}");
		Map<?, ?> map2 = Lang.map("{b:2,a:1}");

		assertTrue(Lang.equals(map1, map2));

		map1 = Lang.map("{a:'1',b:2}");
		map2 = Lang.map("{b:2,a:1}");

		assertFalse(Lang.equals(map1, map2));
	}

	@Test
	public void test_string_array_to_int_array() {
		int[] is = (int[]) Lang.array2array(Lang.array("10", "20"), int.class);
		assertEquals(10, is[0]);
		assertEquals(20, is[1]);
	}

	@Test
	public void test_concat() {
		String[] ss = Lang.array("A", "B");
		assertEquals("A--B", Lang.concat("--", ss).toString());
	}

	public static class BC {
		String name;
		CB cb;
	}

	public static class CB {
		String code;
		BC bc;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_obj2map() {
		BC bc = new BC();
		bc.name = "B";
		CB cb = new CB();
		cb.code = "C";
		bc.cb = cb;
		cb.bc = bc;
		Map<String, Object> map = Lang.obj2map(bc);
		assertEquals("B", map.get("name"));
		assertEquals("C", ((Map) map.get("cb")).get("code"));
		assertNull(((Map) map.get("cb")).get("bc"));
	}
}