package net.coderobe.teb.demo.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.coderobe.teb.demo.iface.InputHandler;

public class CLIInput implements InputHandler {
	private BufferedReader in;
	public CLIInput(InputStream is) {
		in = new BufferedReader(new InputStreamReader(is));
	}
	public CLIInput() {
		this(System.in);
	}
	public boolean hasKey() {
		try {
			return in.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public char getKey() {
		try {
			return (char)in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ' ';
	}
}
