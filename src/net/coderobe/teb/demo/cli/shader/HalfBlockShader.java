package net.coderobe.teb.demo.cli.shader;

import java.awt.Color;

import net.coderobe.teb.demo.iface.CLIShader;

public class HalfBlockShader implements CLIShader { // TODO
	public String color(int x, int y, int rgb, String input) {
		StringBuilder strb = new StringBuilder();
		Color c = new Color(rgb);
		if(y % 2 == 0) {
			strb.append("\u2584"); // upper half block
		} else {
			strb.append("\033[1A"); // up one line
			strb.append(
				"\033[48;2;" + // ANSI 256-color background set RGB
				c.getRed() + ";" +
				c.getGreen() + ";" +
				c.getBlue() + "m"
			);
			strb.append("\033["+input.length()+"C"); // move right
			strb.append("\033[49m"); // reset bg
			//strb.append("\033[1B"); // move down
		}
		return strb.toString();
	}
}
