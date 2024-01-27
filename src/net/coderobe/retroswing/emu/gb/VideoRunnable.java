package net.coderobe.retroswing.emu.gb;

public class VideoRunnable implements Runnable {
	private Core core;
	public VideoRunnable(Core c) {
		core = c;

		System.out.println("Starting gpu");
		core.gpu.start();
	}
	public void run() {
		while(true) {
			core.gpu.render();
		}
	}
}
