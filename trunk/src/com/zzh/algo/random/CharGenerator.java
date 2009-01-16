package com.zzh.algo.random;

/**
 * Generted one char
 * 
 * @author zozoh
 * 
 */
public class CharGenerator {
	private static char[] src = "1234567890_ABCDEFGHI GKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz"
			.toCharArray();

	public static char next() {
		int pos = Math.abs(GM.rnd().nextInt());
		return src[pos % src.length];
	}
}
