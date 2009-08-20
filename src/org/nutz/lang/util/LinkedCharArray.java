package org.nutz.lang.util;

import java.util.ArrayList;

import org.nutz.lang.Lang;

public class LinkedCharArray {

	public LinkedCharArray() {
		this(256);
	}

	public LinkedCharArray(int size) {
		if (size < 0)
			Lang.makeThrow("width must >0!");
		this.width = size;
		cache = new ArrayList<char[]>();
	}

	public LinkedCharArray(String s) {
		this(s.length());
		char[] cs = s.toCharArray();
		cache.add(cs);
		cursor = cs.length;
	}

	private int offset;
	private int cursor;
	private int width;
	private ArrayList<char[]> cache;

	public LinkedCharArray push(int e) {
		return push((char) e);
	}

	public LinkedCharArray push(char e) {
		char[] array;
		int row = cursor / width;
		int i = cursor % width;
		if (i == 0) {
			array = new char[width];
			cache.add(array);
		} else {
			array = cache.get(row);
		}
		array[i] = e;
		cursor++;
		return this;
	}

	public LinkedCharArray push(String s) {
		char[] cs = s.toCharArray();
		for (char c : cs)
			push(c);
		return this;
	}

	public char popFirst() {
		if (size() == 0)
			return (char) 0;
		return innerGet(offset++);
	}

	public String popFirst(int num) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++)
			sb.append(popFirst());
		return sb.toString();
	}

	public char popLast() {
		if (size() == 0)
			return (char) 0;
		return innerGet(--cursor);
	}

	public LinkedCharArray popLast(int num) {
		for (int i = 0; i < num; i++)
			popLast();
		return this;
	}

	public char first() {
		if (size() == 0)
			return (char) 0;
		return innerGet(offset);
	}

	public char last() {
		if (size() == 0)
			return (char) 0;
		return innerGet(cursor - 1);
	}

	public LinkedCharArray set(int index, char e) {
		checkBound(index);
		index += offset;
		char[] array = cache.get(index / width);
		array[index % width] = e;
		return this;
	}

	private void checkBound(int index) {
		if (index >= size() || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
	}

	public String clear() {
		String re = this.toString();
		cache.clear();
		cursor = 0;
		offset = 0;
		return re;
	}

	private char innerGet(int index) {
		char[] array = cache.get(index / width);
		return array[index % width];
	}

	public char get(int index) {
		checkBound(index);
		return innerGet(index + offset);
	}

	public boolean isEmpty() {
		return 0 == cursor - offset;
	}

	public int size() {
		return cursor - offset;
	}

	public boolean startsWith(String s) {
		if (null == s)
			return false;
		if (s.length() > this.size())
			return false;
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i++)
			if (cs[i] != get(i))
				return false;
		return true;
	}

	public boolean endsWith(String s) {
		if (null == s)
			return false;
		if (s.length() > this.size())
			return false;
		char[] cs = s.toCharArray();
		int of = size() - cs.length;
		for (int i = 0; i < cs.length; i++)
			if (cs[i] != get(of + i))
				return false;
		return true;
	}

	public int[] toIntArray() {
		int[] re = new int[size()];
		for (int i = 0; i < re.length; i++)
			re[i] = this.get(i);
		return re;
	}

	public char[] toArray() {
		char[] re = new char[size()];
		for (int i = 0; i < re.length; i++)
			re[i] = (char) this.get(i);
		return re;
	}

	public String toString() {
		return new String(toArray());
	}
}
