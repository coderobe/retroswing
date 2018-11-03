package net.coderobe.retroswing.cli;

import net.coderobe.retroswing.Util;

public class CLICleanupHook extends Thread {
	public void run() {
		// Fix up terminal if previously set to raw
		Util.termCooked();
		System.out.println();
	}
}
