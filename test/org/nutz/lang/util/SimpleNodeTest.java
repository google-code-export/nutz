package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SimpleNodeTest {

	private static Node<String> n(String s) {
		return Nodes.create(s);
	}

	Node<String> root;
	Node<String> A;
	Node<String> B;
	Node<String> C;
	Node<String> D;
	Node<String> E;
	Node<String> F;

	@Before
	public void setup() {
		root = n("root");
		A = n("A");
		B = n("B");
		C = n("C");
		D = n("D");
		E = n("E");
		F = n("F");
	}

	@Test
	public void testGet() {
		assertEquals("A", n("A").get());
	}

	@Test
	public void testSet() {
		assertEquals("B", n("A").set("B").get());
	}

	@Test
	public void testGetAncestors() {
		root.add(A.add(B.add(C)));

		assertEquals(B, C.parent());
		assertEquals(A, B.parent());
		assertEquals(root, A.parent());
		List<Node<String>> ans = C.getAncestors();
		assertEquals(B, ans.get(0));
		assertEquals(A, ans.get(1));
		assertEquals(root, ans.get(2));

	}

	@Test
	public void testDepth() {
		root.add(A.add(B.add(C)));
		assertEquals(3, C.depth());
	}

	@Test
	public void testGetNextSibling() {
		root.add(A, B, C, D, E);
		List<Node<String>> ns = C.getNextSibling();
		assertEquals(2, ns.size());
		assertEquals(D, ns.get(0));
		assertEquals(E, ns.get(1));

		assertEquals(0, E.getNextSibling().size());
	}

	@Test
	public void testGetPrevSibling() {
		root.add(A, B, C, D, E);
		List<Node<String>> ns = C.getPrevSibling();
		assertEquals(2, ns.size());
		assertEquals(B, ns.get(0));
		assertEquals(A, ns.get(1));

		assertEquals(0, A.getPrevSibling().size());
	}

	@Test
	public void testIndex() {
		root.add(A, B, C, D, E);
		assertEquals(0, A.index());
		assertEquals(1, B.index());
		assertEquals(2, C.index());
		assertEquals(3, D.index());
		assertEquals(4, E.index());
	}

	@Test
	public void testGetChildren() {
		root.add(A, B, C, D, E);
		assertEquals(5, root.countChildren());
		List<Node<String>> children = root.getChildren();
		assertEquals(5, children.size());
		assertEquals(A, children.get(0));
		assertEquals(B, children.get(1));
		assertEquals(C, children.get(2));
		assertEquals(D, children.get(3));
		assertEquals(E, children.get(4));
	}

	@Test
	public void testAddFirst() {
		root.add(B, C, D);
		root.addFirst(A);
		root.add(E);
		assertEquals(5, root.countChildren());
		List<Node<String>> children = root.getChildren();
		assertEquals(5, children.size());
		assertEquals(A, children.get(0));
		assertEquals(B, children.get(1));
		assertEquals(C, children.get(2));
		assertEquals(D, children.get(3));
		assertEquals(E, children.get(4));
	}

	@Test
	public void testChild() {
		root.add(A, B, C, D, E);
		assertNull(root.child(-1));
		assertNull(root.child(5));
		assertEquals(A, root.child(0));
		assertEquals(B, root.child(1));
		assertEquals(C, root.child(2));
		assertEquals(D, root.child(3));
		assertEquals(E, root.child(4));
	}

	@Test
	public void testDesc() {
		A.add(B, C);
		D.add(E, F);
		root.add(A, D);
		assertEquals(A, root.desc(0));
		assertEquals(B, root.desc(0, 0));
		assertEquals(C, root.desc(0, 1));
		assertEquals(D, root.desc(1));
		assertEquals(E, root.desc(1, 0));
		assertEquals(F, root.desc(1, 1));
	}

	@Test
	public void testInsert() {
		root.add(A, C, E);
		root.insertBefore(1, B);
		assertEquals(A, root.child(0));
		assertEquals(B, root.child(1));
		assertEquals(C, root.child(2));
		assertEquals(E, root.child(3));

		root.insertBefore(3, D);
		assertEquals(A, root.child(0));
		assertEquals(B, root.child(1));
		assertEquals(C, root.child(2));
		assertEquals(D, root.child(3));
		assertEquals(E, root.child(4));

		assertEquals(5, root.countChildren());
	}

	@Test
	public void testPop() {
		root.add(A, B, C, D, E);
		assertEquals(E, root.pop());
		assertEquals(D, root.pop());
		assertEquals(C, root.pop());
		assertEquals(B, root.pop());
		assertEquals(A, root.pop());
		assertNull(root.pop());
		assertNull(root.pop());
		assertNull(root.child(0));
		assertEquals(0, root.countChildren());
	}

	@Test
	public void testPopFirst() {
		root.add(A, B, C, D, E);
		assertEquals(A, root.popFirst());
		assertEquals(B, root.popFirst());
		assertEquals(C, root.popFirst());
		assertEquals(D, root.popFirst());
		assertEquals(E, root.popFirst());
		assertNull(root.pop());
		assertNull(root.pop());
		assertNull(root.child(0));
		assertEquals(0, root.countChildren());
	}

	@Test
	public void testRemove() {
		root.add(A, B, C, D, E);
		assertEquals(C, root.remove(2));
		assertEquals(A, root.child(0));
		assertEquals(B, root.child(1));
		assertEquals(D, root.child(2));
		assertEquals(E, root.child(3));

		assertEquals(E, root.remove(3));
		assertEquals(A, root.child(0));
		assertEquals(B, root.child(1));
		assertEquals(D, root.child(2));

		assertEquals(A, root.remove(0));
		assertEquals(B, root.child(0));
		assertEquals(D, root.child(1));

		assertNull(root.remove(5));
		assertEquals(B, root.child(0));
		assertEquals(D, root.child(1));

		assertNull(root.remove(-1));
		assertEquals(B, root.child(0));
		assertEquals(D, root.child(1));

		assertEquals(2, root.countChildren());

		assertEquals(B, root.remove(0));
		assertEquals(D, root.remove(0));

		assertTrue(root.isEmpty());
	}

}
