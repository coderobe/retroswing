package net.coderobe.teb.demo.gui;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Renderer;

public class SwingRenderer implements Renderer {
	private Framebuffer fb;
	private BufferedImage view;
	private Image view_img;
	private boolean init = false;
	private JFrame window = new JFrame();
	private JLabel view_label;
	private int zoom;
	public SwingRenderer(Framebuffer f) {
		this(f, 1);
	}
	public SwingRenderer(Framebuffer f, int z) {
		fb = f;
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setZoom(z);
	}
	public void setZoom(int z) {
		zoom = z;
		view = new BufferedImage(fb.getWidth(), fb.getHeight(), BufferedImage.TYPE_INT_RGB);
	}
	private void replace() {
		view_img = new ImageIcon(view).getImage().getScaledInstance(fb.getWidth()*zoom, fb.getHeight()*zoom, java.awt.Image.SCALE_REPLICATE);
		view_label.setIcon(new ImageIcon(view_img));
		window.getContentPane().add(view_label);
	}
	public JFrame getWindow() {
		return window;
	}
	public void draw() {
		if(!init) {
			init = true;
			window.getContentPane().setLayout(new FlowLayout());
			view_label = new JLabel();
			replace();
			window.pack();
			window.setVisible(true);
		}
		for(int y = 0; y < fb.getHeight(); y++) {
			for(int x = 0; x < fb.getWidth(); x++) {
				view.setRGB(x, y, fb.getRGB(x, y)); // TODO: shaders
			}
		}
		replace();
		window.repaint();
	}
	public void close() {
		window.dispose();
	}
}
