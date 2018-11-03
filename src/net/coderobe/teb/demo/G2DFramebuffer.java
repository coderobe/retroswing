package net.coderobe.teb.demo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Sprite;

public class G2DFramebuffer implements Framebuffer {
	private int height;
	private int width;
	private BufferedImage buffer;
	private BufferedImage buffer_old;
	private static BufferedImage copyBuffer(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	public boolean damaged() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (buffer.getRGB(x, y) != buffer_old.getRGB(x, y)) {
					return true;
				}
			}
		}
		return false;
	}
	public G2DFramebuffer(int w, int h) {
		width = w;
		height = h;
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	public int getRGB(int x, int y) {
		try {
			return buffer.getRGB(x, y);
		} catch(ArrayIndexOutOfBoundsException e) {
			return new Color(0, 0, 0).getRGB();
		}
	}
	public void setRGB(int x, int y, int rgb) {
		try {
			buffer.setRGB(x, y, rgb);
		} catch(ArrayIndexOutOfBoundsException e) {}
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public void blit(Sprite b) {
		for(int y = 0; y < this.getHeight(); y++) {
			for(int x = 0; x < this.getWidth(); x++) {
				if(b.isOn(x, y)) {
					this.setRGB(x, y, b.getRGB(x, y));
				}
			}
		}
	}
	public void clear() {
		buffer_old = G2DFramebuffer.copyBuffer(buffer);
		for(int y = 0; y < this.getHeight(); y++) {
			for(int x = 0; x < this.getWidth(); x++) {
				this.setRGB(x, y, new Color(0, 0, 0).getRGB());
			}
		}
	}
}
