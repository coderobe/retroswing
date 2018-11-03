package net.coderobe.retroswing.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import net.coderobe.retroswing.iface.InputHandler;

public class SwingInput extends KeyAdapter implements InputHandler {
	private JFrame window;
	private char lastkey;
	public SwingInput(JFrame f){
		window = f;
		window.addKeyListener(this);
	}
	public void keyPressed(KeyEvent e) {
		lastkey = e.getKeyChar();
	}
	public boolean hasKey() {
		return lastkey != '\0';
	}

	public char getKey() {
		char k = lastkey;
		lastkey = '\0';
		return k;
	}
}
