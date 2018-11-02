package net.coderobe.teb.demo.cli.shader;

import java.util.ArrayList;
import java.util.List;

import net.coderobe.teb.demo.iface.CLIShader;

public class MultiShader implements CLIShader {
	private List<CLIShader> chain = new ArrayList<CLIShader>();

	public void add(CLIShader c) {
		chain.add(c);
	}
	
	public String color(int x, int y, int rgb, String input) {
		for(CLIShader c : chain) {
			input = c.color(x, y, rgb, input);
		}
		return input;
	}

}
