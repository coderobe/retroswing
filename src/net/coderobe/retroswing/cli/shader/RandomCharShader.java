package net.coderobe.retroswing.cli.shader;

import java.util.Random;

import net.coderobe.retroswing.iface.CLIShader;

public class RandomCharShader implements CLIShader {
	public String color(int x, int y, int rgb, String input) {
		StringBuilder strb = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			strb.append((input.charAt(i) == ' ') ? ' ' : (char) (97 + new Random().nextInt(25)));
		}
		return strb.toString();
	}
}
