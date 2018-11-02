package net.coderobe.teb.demo.sprite;

import java.awt.Color;

import net.coderobe.teb.demo.iface.Sprite;

public class Rect implements Sprite {
	private int width;
	private int height;
	private int x;
	private int y;
	private int color;
	public Rect(int w, int h, int p_x, int p_y) {
		width = w;
		height = h;
		x = p_x;
		y = p_y;
		color = new Color(255, 255, 255).getRGB();
	}
	public Rect(int w, int h) {
		this(w, h, 0, 0);
	}
	public void setColor(int rgb) {
		color = rgb;
	}
	public int getRGB(int x, int y) {
		return color;
	}
	public boolean isOn(int p_x, int p_y) {
		return (p_x >= x && p_x <= x+width-1) && (p_y >= y && p_y <= y+height-1);
	}
	public void setPos(int p_x, int p_y) {
		x = p_x;
		y = p_y;
	}
	public void setRel(int p_x, int p_y) {
		x += p_x;
		y += p_y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
