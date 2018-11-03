package net.coderobe.retroswing.iface;

public interface Framebuffer {
	int getWidth();
	int getHeight();
	int getRGB(int x, int y);
	void setRGB(int x, int y, int rgb);
	void blit(Sprite b);
	void clear();
	boolean damaged();
}
