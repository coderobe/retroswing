package net.coderobe.retroswing.emu.gb;

import java.awt.Color;

import net.coderobe.retroswing.G2DFramebuffer;
import net.coderobe.retroswing.cli.HalfBlockRenderer;
import net.coderobe.retroswing.cli.SixelRenderer;
import net.coderobe.retroswing.gui.SwingRenderer;
import net.coderobe.retroswing.iface.Framebuffer;
import net.coderobe.retroswing.iface.Renderer;

public class Video {
	public Memory mmu;
	public int width = 256;
	public int height = 256;
	public Framebuffer fb;
	public Renderer renderer;
	public int[] color = new int[] {
			new Color(12, 16, 26).getRGB(),
			new Color(44, 104, 79).getRGB(),
			new Color(137, 194, 112).getRGB(),
			new Color(205, 228, 189).getRGB()
	};
	public Video(Memory m) {
		mmu = m;
	}
	public void start() {
		fb = new G2DFramebuffer(width, height);
		renderer = new SwingRenderer(fb, 2);
		//renderer = new HalfBlockRenderer(fb);
		//renderer = new SixelRenderer(fb);

	}
	public void stop() {
		renderer.close();
	}
	public void render() {
		if(get_lcd_on()) { // lcd is operating
			//System.err.println("Video.render");
			fb.clear();
			
			if(true || get_display_on()) { // screen is on
				//System.err.println("Video.render: display");

				short tile_map_bg = get_tile_map_bg();
				short tile_data_addr = get_tile_data();
				
				for(int ty = 0; ty < 32; ty++) {
					for(int tx = 0; tx < 32; tx++) {
						byte tile_addr = mmu.ram.get((short)(tile_map_bg + tx + ty*32));
						for(int y = 0; y < 8; y++) {
							short line_addr = (short)(tile_data_addr + Byte.toUnsignedInt(tile_addr)*16 + y*2);
							short line2_addr = (short)(tile_data_addr + Byte.toUnsignedInt(tile_addr)*16 + y*2 + 1);
							for(int x = 0; x < 8; x++) {
								boolean upper_bit = mmu.get_bit(line_addr, 7-x);
								boolean lower_bit = mmu.get_bit(line2_addr, 7-x);
								int c;
								if(upper_bit && lower_bit) {
									c = color[0];
								} else if(lower_bit) {
									c = color[1];
								} else if(upper_bit) {
									c = color[2];
								} else {
									c = color[3];
								}

								fb.setRGB(tx * 8 + x, ty * 8 + y, c);
							}
						}
					}
				}
				
				if(get_window_on()) { // window should be rendered
					//System.err.println("Video.render: window");
					
					// TODO
				}
			}
			
			renderer.draw();
		}
	}
	private boolean get_lcd_on() {
		return mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 7);
	}
	private boolean get_display_on() {
		return mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 0);
	}
	private boolean get_window_on() {
		return mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 5);
	}
	private short get_tile_map_bg() {
		return (mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 3) ? (short) 0x9C00 : (short) 0x9800);
	}
	private short get_tile_map_win() {
		return (mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 6) ? (short) 0x9C00 : (short) 0x9800);
	}
	private short get_tile_data() {
		return (!mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 4) ? (short) 0x8000 : (short) 0x8800);
	}
}
