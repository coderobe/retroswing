package net.coderobe.teb.demo.cli;

import java.awt.Color;
import java.io.PrintStream;

import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Renderer;

public class HalfBlockRenderer implements Renderer {
	private Framebuffer fb;
	private PrintStream ps;
	private boolean init = false;
	public HalfBlockRenderer(Framebuffer f, PrintStream p) {
		fb = f;
		ps = p;
	}
	public HalfBlockRenderer(Framebuffer f) {
		this(f, System.out);
	}
	public void draw() {
		if(!init) { // move cursor Y lines downwards once to prevent mangling of previous output
			for(int y = 0; y < fb.getHeight(); y+=2) {
				ps.println();
			}
			ps.print("\033[?25l"); // hide cursor
			init = true;
		}
		StringBuilder strb = new StringBuilder();
		Color c_top_prev = null;
		Color c_bot_prev = null;
		strb.append("\033[;H"); // ANSI cursor to home position
		for(int y = 0; y < fb.getHeight(); y+=2) {
			//strb.append("\033[" + (y+1) + ";" + 0 + "f");
			for(int x = 0; x < fb.getWidth(); x++) {
				Color c_top = new Color(fb.getRGB(x, y));
				Color c_bot = new Color(fb.getRGB(x, y+1));
				if(!c_top.equals(c_top_prev)) { // don't set fg color when already on correct color
					strb.append(
						"\033[38;2;" + // ANSI 256-color foreground set RGB
						c_top.getRed() + ";" +
						c_top.getGreen() + ";" +
						c_top.getBlue() + "m"
					);
					c_top_prev = c_top;
				}
				if(!c_bot.equals(c_bot_prev)) { // don't set bg color when already on correct color
					if(y+1 >= fb.getHeight()) {
						// lower half of last row unused, reset color
						strb.append("\033[49m");
					} else {
						strb.append(
							"\033[48;2;" + // ANSI 256-color background set RGB
							c_bot.getRed() + ";" +
							c_bot.getGreen() + ";" +
							c_bot.getBlue() + "m"
						);
					}
					c_bot_prev = c_bot;
				}
				strb.append("\u2580"); // Unicode UPPER HALF BLOCK (▀)
			}
			strb.append("\033[1B\033["+fb.getWidth()+"D"); // move down one line and to X=0
		}
		ps.print(strb);
		ps.flush(); // necessary?
	}
	public void close() {
		ps.print("\033[0m"); // reset graphics rendition
		ps.print("\033[?25h"); // show cursor
	}
}
