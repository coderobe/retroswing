package net.coderobe.teb.demo.iface;

public interface Sprite {
	int getRGB(int x, int y);
	boolean isOn(int x, int y);
	void setPos(int x, int y);
	void setRel(int x, int y);
	int getX();
	int getY();
	int getWidth();
	int getHeight();
}
