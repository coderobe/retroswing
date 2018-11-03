package net.coderobe.retroswing.cli.shader;

import net.coderobe.retroswing.iface.CLIShader;

public class BlockCharShader implements CLIShader {
	public String color(int x, int y, int rgb, String input) {
		StringBuilder strb = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			strb.append((input.charAt(i) == ' ') ? ' ' : '█');
		}
		return strb.toString();
	}
}
