package net.coderobe.retroswing.emu.gb;

public class VideoRunnable implements Runnable {
	private Core core;
	public VideoRunnable(Core c) {
		core = c;

		System.out.println("Starting gpu");
		core.gpu.start();
	}
	public void run() {
		long t_start = System.nanoTime();
		while(true) {
			// wait for next frame time on 30Hz bounds
			long t_end = System.nanoTime();
			core.gpu.render();
			try {
				Thread.sleep(Math.max(0, (long) (33.33 - ((System.nanoTime() - t_end) / 1e6))));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t_start = t_end;
		}
	}
}
