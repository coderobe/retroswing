package net.coderobe.teb.demo.cli;

import java.io.PrintStream;

import net.coderobe.teb.demo.cli.shader.BoolShader;
import net.coderobe.teb.demo.iface.CLIShader;
import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Renderer;

public class CLIRenderer implements Renderer {
	private Framebuffer fb;
	private PrintStream ps;
	private CLIShader col;
	private boolean init = false;
	public CLIRenderer(Framebuffer f, PrintStream p, CLIShader c) {
		fb = f;
		ps = p;
		col = c;
	}
	public CLIRenderer(Framebuffer f, PrintStream p) {
		this(f, p, new BoolShader());
	}
	public CLIRenderer(Framebuffer f, CLIShader c) {
		this(f, System.out, c);
	}
	public CLIRenderer(Framebuffer f) {
		this(f, System.out);
	}
	public void draw() {
		if(!init) { // move cursor Y lines downwards once to prevent mangling of previous output
			for(int y = 0; y < fb.getHeight(); y++) {
				ps.println();
			}
			init = true;
		}
		StringBuilder strb = new StringBuilder();
		strb.append("\033[?25l"); // hide cursor
		strb.append("\033[;H"); // ANSI cursor to home position
		for(int y = 0; y < fb.getHeight(); y++) {
			strb.append("\033[" + (y+1) + ";" + 0 + "f");
			for(int x = 0; x < fb.getWidth(); x++) {
				strb.append(col.color(x, y, fb.getRGB(x, y), "##"));
			}
		}
		//strb.append("\033[1;0f" + fb.getWidth() + "x" + fb.getHeight()); // debug info
		//strb.append("\033[" + fb.getHeight() + ";" + 0 + "f");
		strb.append("\033[?25h"); // show cursor
		ps.print(strb);
		ps.flush(); // necessary?
	}
	public void close() {
	}
}
