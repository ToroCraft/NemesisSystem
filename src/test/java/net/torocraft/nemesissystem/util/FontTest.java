package net.torocraft.nemesissystem.util;

import org.junit.Test;

public class FontTest {
	@Test
	public void test() {
		int c = 1;
		for (int i =0x2d30; i <= 0x2d66; i++) {
			System.out.println(c++ + ") " + Integer.toHexString(i) + " => " + (char)i);
		}
	}
}
