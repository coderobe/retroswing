package net.coderobe.retroswing.cli.shader;

import java.awt.Color;

import net.coderobe.retroswing.iface.CLIShader;

public class ANSIShader implements CLIShader {
	public String color(int x, int y, int rgb, String input) {
		StringBuilder strb = new StringBuilder();
		Color c = new Color(rgb);
		strb.append(
			"\033[38;2;" + // ANSI 256-color foreground set RGB
			c.getRed() + ";" +
			c.getGreen() + ";" +
			c.getBlue() + "m" +
			input
		);
		return strb.toString();
	}
}
