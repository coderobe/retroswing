package net.coderobe.teb.demo.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.coderobe.teb.demo.iface.Framebuffer;
import net.coderobe.teb.demo.iface.Renderer;

public class SwingRenderer implements Renderer {
	private Framebuffer fb;
	private BufferedImage fbData;
	private int zoom;

	private JFrame window;
	private JPanel viewPanel;

	public SwingRenderer(Framebuffer fb) {
		this(fb, 1);
	}

	public SwingRenderer(Framebuffer fb, int zoom) {
		if (fb == null) {
			throw new IllegalArgumentException("Framebuffer must not be null!");
		}

		this.fb = fb;
		this.zoom = zoom;

		fbData = new BufferedImage(fb.getWidth() * zoom, fb.getHeight() * zoom,
				BufferedImage.TYPE_INT_RGB);

		viewPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(fbData, 0, 0, null);
			}
		};

		viewPanel.setPreferredSize(new Dimension(fbData.getWidth(), fbData.getHeight()));
		viewPanel.setSize(fbData.getWidth(), fbData.getHeight());

		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new FlowLayout());
		window.getContentPane().add(viewPanel);
		window.pack();
		window.setVisible(true);
	}

	public int getZoom() {
		return zoom;
	}

	public synchronized void setZoom(int zoom) {
		this.zoom = zoom;
		fbData = new BufferedImage(fb.getWidth() * zoom, fb.getHeight() * zoom,
				BufferedImage.TYPE_INT_RGB);
	}

	public JFrame getWindow() {
		return window;
	}

	public synchronized void draw() {
		for (int x = 0; x < fb.getWidth() * zoom; x++) {
			for (int y = 0; y < fb.getHeight() * zoom; y++) {
				fbData.setRGB(x, y, fb.getRGB(x / zoom, y / zoom));
			}
		}

		window.repaint();
	}

	public void close() {
		window.dispose();
	}
}
