package org.nutz.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.nutz.lang.Maths.*;

public class MathsTest {

	@Test
	public void testIsMask() {
		assertTrue(isMask(bit("100"), bit("110")));
		assertFalse(isMask(bit("100"), bit("10010")));
	}

	@Test
	public void testIsMaskAll() {
		assertFalse(isMaskAll(bit("100"), bit("110")));
		assertTrue(isMaskAll(bit("100101111"), bit("001110")));
	}

	@Test
	public void test_extract_int() {
		assertEquals(3, Maths.extract(7, 1, 3));
		assertEquals(1, Maths.extract(7, 0, 1));
		assertEquals(3, Maths.extract(255, 4, 6));
	}

	@Test
	public void test_is_not_mask_all() {
		assertFalse(isNoMask(bit("0110"), bit("1100")));
		assertFalse(isNoMask(bit("0100"), bit("1100")));
		assertFalse(isNoMask(bit("1000"), bit("1100")));
		assertTrue(isNoMask(bit("110011"), bit("1100")));
		assertFalse(isNoMask(bit("111011"), bit("1100")));
	}

	@Test
	public void test_find_in_cs_0() {
		char[] cs = "".toCharArray();
		assertEquals(-1, Maths.find(cs, 'c'));

		cs = "A".toCharArray();
		assertEquals(-1, Maths.find(cs, 'c'));

		cs = "AB".toCharArray();
		assertEquals(-1, Maths.find(cs, 'c'));
	}

	@Test
	public void test_find_in_cs_1() {
		char[] cs = "ABCDE".toCharArray();
		assertEquals(0, Maths.find(cs, 'A'));
		assertEquals(1, Maths.find(cs, 'B'));
		assertEquals(2, Maths.find(cs, 'C'));
		assertEquals(3, Maths.find(cs, 'D'));
		assertEquals(4, Maths.find(cs, 'E'));
		assertEquals(-1, Maths.find(cs, 'F'));
	}

	@Test
	public void test_find_in_cs_2() {
		char[] cs = "ABCDEF".toCharArray();
		assertEquals(0, Maths.find(cs, 'A'));
		assertEquals(1, Maths.find(cs, 'B'));
		assertEquals(2, Maths.find(cs, 'C'));
		assertEquals(3, Maths.find(cs, 'D'));
		assertEquals(4, Maths.find(cs, 'E'));
		assertEquals(5, Maths.find(cs, 'F'));
		assertEquals(-1, Maths.find(cs, '*'));
	}
}
