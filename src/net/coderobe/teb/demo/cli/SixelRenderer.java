package net.coderobe.teb.demo.cli;

import java.awt.Color;
import java.io.PrintStream;
import java.util.HashMap;

import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Renderer;

public class SixelRenderer implements Renderer {
	private Framebuffer fb;
	private PrintStream ps;
	private boolean init = false;
	private int zoom;
	private int black = new Color(0, 0, 0).getRGB();
	public SixelRenderer(Framebuffer f, PrintStream p, int z) {
		fb = f;
		ps = p;
		zoom = z;
	}
	public SixelRenderer(Framebuffer f, PrintStream p) {
		this(f, p, 1);
	}
	public SixelRenderer(Framebuffer f, int z) {
		this(f, System.out, z);
	}
	public SixelRenderer(Framebuffer f) {
		this(f, System.out);
	}
	public void draw() {
		if(!fb.damaged()) return;
		if(!init) { // move cursor Y lines downwards once to prevent mangling of previous output
			for(int y = 0; y < fb.getHeight() / 3; y++) { // one line is about 3 sixels high
				ps.println();
			}
			ps.print("\033[?25l"); // hide cursor
			init = true;
		}
		StringBuilder strb = new StringBuilder();
		HashMap<Integer, Integer> palette = new HashMap<Integer, Integer>();
		strb.append("\033[;H"); // ANSI cursor to home position
		strb.append("\033Pq"); // enter sixel mode
		for(int y = 0; y < fb.getHeight() * zoom; y++) {
			strb.append("!" + (y+1) + "$-"); // move to line
			for(int x = 0; x < fb.getWidth(); x++) {
				strb.append("!" + (6 * zoom));
				int pixcol = fb.getRGB(x, y / zoom);
				if(pixcol != black) {
					Color pc = new Color(pixcol);
					if(palette.get(pixcol) == null) {
						// map color to palette index if not exists
						palette.put(pixcol, palette.size());
						// register color to palette index in sixel
						strb.append("#"+palette.get(pixcol)+";2;"+pc.getRed()+";"+pc.getGreen()+";"+pc.getBlue());
					}
					// draw full sixel with palette id of current color
					strb.append("#"+palette.get(pixcol)+"~");
				} else {
					// draw empty sixel
					strb.append("?");
				}
			}
		}
		strb.append("\033\\"); // leave sixel mode
		ps.print(strb);
		ps.flush(); // necessary?
	}
	public void close() {
		ps.print("\033[?25.h"); // show cursor
	}
}
