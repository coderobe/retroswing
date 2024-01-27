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
	public int[] color = new int[]{
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
	}

	public void stop() {
		renderer.close();
	}

	public void render() {
		long render_start = System.nanoTime();
		if (get_lcd_on()) {
			//fb.clear(); // TODO: clean up or maake fast

			// Iterate through each scanline
			for (int ly = 0; ly < 144; ly++) {
				mmu.ram.put(mmu.reg_io_loc.get("LY"), (byte) ly);
				if (true /* <- TODO: remove me without breaking it */ || get_display_on()) {
					renderScanline(ly);
				}
			}

			triggerVBlankInterrupt();

			// Vertical blanking interval
			try { // wait for vblank on 60Hz bounds
				Thread.sleep(Math.max(0, (long) (16.7 - ((System.nanoTime() - render_start) / 1e6))));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int ly = 144; ly < 154; ly++) {
				mmu.ram.put(mmu.reg_io_loc.get("LY"), (byte) ly);
			}

			checkAndTriggerLCDStatInterrupt();

			renderer.draw();
		}

		// Reset LY register after frame rendering
		mmu.ram.put(mmu.reg_io_loc.get("LY"), (byte) 0);
	}

	private void triggerVBlankInterrupt() {
		mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (mmu.ram.get(mmu.reg_io_loc.get("IF")) | 0x01)); // Set V-Blank interrupt flag
	}

	private void checkAndTriggerLCDStatInterrupt() {
		byte stat = mmu.ram.get(mmu.reg_io_loc.get("STAT"));
		byte ly = mmu.ram.get(mmu.reg_io_loc.get("LY"));
		byte lyc = mmu.ram.get(mmu.reg_io_loc.get("LYC"));

		boolean interruptRequested = false;

		// LY=LYC Check
		if ((stat & 0x40) != 0 && ly == lyc) {
			interruptRequested = true;
		}

		// Mode 2 OAM Check
		if ((stat & 0x20) != 0) {
			interruptRequested = true;
		}

		// Mode 1 V-Blank Check
		if ((stat & 0x10) != 0 && ly >= 144) { // V-Blank starts from line 144
			interruptRequested = true;
		}

		// Mode 0 H-Blank Check
		if ((stat & 0x08) != 0) {
			interruptRequested = true;
		}

		if (interruptRequested) {
			mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (mmu.ram.get(mmu.reg_io_loc.get("IF")) | 0x02)); // Set STAT interrupt flag
		}
	}

	private void renderScanline(int ly) {
		if (get_window_on()) {
			// TODO: Render window for the current scanline
		}
		// Render background for the current scanline
		renderBackground(ly);
	}

	private void renderBackground(int ly) {
		short tile_map_bg = get_tile_map_bg();
		short tile_data_addr = get_tile_data();

		for (int ty = 0; ty < 32; ty++) {
			for (int tx = 0; tx < 32; tx++) {
				renderTile(tile_map_bg, tile_data_addr, tx, ty);
			}
		}
	}

	private void renderTile(short tileMapBg, short tileDataAddr, int tx, int ty) {
		byte tileAddr = mmu.ram.get((short) (tileMapBg + tx + ty * 32));
		int tileBaseAddr = Byte.toUnsignedInt(tileAddr) * 16;

		for (int y = 0; y < 8; y++) {
			short lineAddr = (short) (tileDataAddr + tileBaseAddr + y * 2);
			short line2Addr = (short) (tileDataAddr + tileBaseAddr + y * 2 + 1);

			for (int x = 0; x < 8; x++) {
				int colorIndex = getColorIndex(lineAddr, line2Addr, x);
				fb.setRGB(tx * 8 + x, ty * 8 + y, color[colorIndex]);
			}
		}
	}

	private int getColorIndex(short lineAddr, short line2Addr, int x) {
		boolean upperBit = mmu.get_bit(lineAddr, 7 - x);
		boolean lowerBit = mmu.get_bit(line2Addr, 7 - x);

		if (upperBit && lowerBit) {
			return 0;
		} else if (lowerBit) {
			return 1;
		} else if (upperBit) {
			return 2;
		} else {
			return 3;
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
		return (mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 6) ? (short) 0x9C00 : (short) 0x9800);
	}

	private short get_tile_data() {
		return (!mmu.get_bit(mmu.reg_io_loc.get("LCDC"), 4) ? (short) 0x8000 : (short) 0x8800);
	}
}
