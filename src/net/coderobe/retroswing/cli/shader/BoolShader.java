package net.coderobe.retroswing.cli.shader;

import java.awt.Color;

import net.coderobe.retroswing.iface.CLIShader;

public class BoolShader implements CLIShader {
	private Color c;
	public BoolShader() {
		this(new Color(0, 0, 0));
	}
	public BoolShader(Color col) {
		c = col;
	}
	public String color(int x, int y, int rgb, String input) {
		StringBuilder strb = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			strb.append((rgb == c.getRGB()) ? " " : input.charAt(i));
		}
		return strb.toString();
	}
}
