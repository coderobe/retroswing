package net.coderobe.retroswing.sprite;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.coderobe.retroswing.iface.Sprite;

public class StringTexturedSprite implements Sprite {
	private int width;
	private int height;
	private int x;
	private int y;
	private String[] texture;
	private Map<String, Color> colormap = new HashMap<String, Color>();
	public StringTexturedSprite(int w, int h, int p_x, int p_y) {
		this();
		width = w;
		height = h;
		x = p_x;
		y = p_y;
	}
	public StringTexturedSprite(int w, int h) {
		this(w, h, 0, 0);
	}
	public StringTexturedSprite() {
		colormap.put("1", new Color(17, 17, 17));
		colormap.put("2", new Color(85, 85, 85));
		colormap.put("3", new Color(204, 204, 204));
		colormap.put("4", new Color(238, 238, 238));
	}
	public void setTexture(String t) {
		texture = t.split("\n");
		height = texture.length;
		Arrays.asList(texture).forEach(texline -> {width = Math.max(width, texline.length());});
	}
	public int getRGB(int p_x, int p_y) {
		return colormap.get(""+texture[p_y - y].charAt(p_x - x)).getRGB();
	}
	public boolean isOn(int p_x, int p_y) {
		return (p_x >= x && p_x <= x+width-1) && (p_y >= y && p_y <= y+height-1) &&
				texture[p_y - y].charAt(p_x - x) != ' ';
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
