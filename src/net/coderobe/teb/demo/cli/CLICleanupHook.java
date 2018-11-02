package net.coderobe.teb.demo.cli;

import net.coderobe.teb.demo.Util;

public class CLICleanupHook extends Thread {
	public void run() {
		// Fix up terminal if previously set to raw
		Util.termCooked();
		System.out.println();
	}
}
