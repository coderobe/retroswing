package net.coderobe.retroswing;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Util {
	public static boolean isUnix() {
		return File.listRoots().length == 1 && File.listRoots()[0].getAbsolutePath() == "/";
	}
	public static int rgbRainbowNext(int rgb) {
		Color c = new Color(rgb);
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
	    if(r >= 255 && g < 255 && b <= 0) g+=2;
	    if(g >= 255 && r > 0   && b <= 0) r-=2;
	    if(g >= 255 && b < 255 && r <= 0) b+=2;
	    if(b >= 255 && g > 0   && r <= 0) g-=2;
	    if(b >= 255 && r < 255 && g <= 0) r+=2;
	    if(r >= 255 && b > 0   && g <= 0) b-=2;
		return new Color(
			Math.max(0, Math.min(255, r)),
			Math.max(0, Math.min(255, g)),
			Math.max(0, Math.min(255, b))
		).getRGB();
	}
	@SuppressWarnings("resource")
	public static int termCols() {
		if(Util.isUnix()) {
			String[] cmd = {"sh", "-c", "tput cols </dev/tty"};
			int out = 0;
		    try {
				Process p = Runtime.getRuntime().exec(cmd);
				out = new Scanner(p.getInputStream()).nextInt();
				p.waitFor();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return out;
		} else {
			return 10; // chosen by a fair dice roll
		}
	}
	@SuppressWarnings("resource")
	public static int termLines() {
		if(Util.isUnix()) {
			String[] cmd = {"sh", "-c", "tput lines </dev/tty"};
			int out = 0;
		    try {
				Process p = Runtime.getRuntime().exec(cmd);
				out = new Scanner(p.getInputStream()).nextInt();
				p.waitFor();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return out;
		} else {
			return 10;
		}
	}
	public static void termRaw() {
		if(Util.isUnix()) {
			String[] cmd = {"sh", "-c", "stty raw </dev/tty; stty -echo </dev/tty"};
		    try {
				Runtime.getRuntime().exec(cmd).waitFor();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void termCooked() {
		if(Util.isUnix()) {
			String[] cmd = {"sh", "-c", "stty cooked </dev/tty; stty echo </dev/tty"};
			try {
				Runtime.getRuntime().exec(cmd).waitFor();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
