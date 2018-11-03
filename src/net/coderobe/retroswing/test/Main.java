package net.coderobe.retroswing.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.coderobe.retroswing.G2DFramebuffer;
import net.coderobe.retroswing.Util;
import net.coderobe.retroswing.cli.CLICleanupHook;
import net.coderobe.retroswing.cli.CLIInput;
import net.coderobe.retroswing.cli.CLIRenderer;
import net.coderobe.retroswing.cli.HalfBlockRenderer;
import net.coderobe.retroswing.cli.SixelRenderer;
import net.coderobe.retroswing.cli.shader.ANSIShader;
import net.coderobe.retroswing.cli.shader.BlockCharShader;
import net.coderobe.retroswing.cli.shader.BoolShader;
import net.coderobe.retroswing.cli.shader.MultiShader;
import net.coderobe.retroswing.cli.shader.RandomCharShader;
import net.coderobe.retroswing.gui.SwingInput;
import net.coderobe.retroswing.gui.SwingRenderer;
import net.coderobe.retroswing.iface.Framebuffer;
import net.coderobe.retroswing.iface.InputHandler;
import net.coderobe.retroswing.iface.Renderer;
import net.coderobe.retroswing.iface.Sprite;
import net.coderobe.retroswing.sprite.Border;
import net.coderobe.retroswing.sprite.Rect;
import net.coderobe.retroswing.sprite.StringTexturedSprite;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		boolean cli = (args.length >= 1) ? new Scanner(args[0]).nextLine().equalsIgnoreCase("cli") : false;
		boolean use_sixel = false;
		int width;
		int height;
		if(cli) {
			width = (args.length >= 2 ? new Scanner(args[1]).nextInt() : Util.termCols()) / 2;
			height = (args.length >= 3 ? new Scanner(args[2]).nextInt() : Util.termLines());
		}else {
			width = 42;
			height = 42;
		}

		if(use_sixel && cli && args.length < 2) { // approximate accounting for sixel size
			width *= 3.3;
			height *= 3;
		}else if(cli && args.length < 2) {
			width *= 2;
			height *=2;
		}

		System.out.println("Width: "+width+", Height: "+height);
		
		Framebuffer fb = new G2DFramebuffer(width, height);
		Renderer r;
		InputHandler in;
		
		if(cli) {
			if(use_sixel) {
				r = new SixelRenderer(fb, 1);
			} else {
				/*
				MultiShader c = new MultiShader();
				c.add(new BoolShader());
				c.add(new BlockCharShader());
				//c.add(new HalfBlockShader()); // TODO
				//c.add(new RandomCharShader());
				c.add(new ANSIShader());

				r = new CLIRenderer(fb, c);
				*/
				r = new HalfBlockRenderer(fb);
			}

			in = new CLIInput();

			Util.termRaw();
			Runtime.getRuntime().addShutdownHook(new CLICleanupHook());
		} else {
			r = new SwingRenderer(fb, 16);
			in = new SwingInput(((SwingRenderer)r).getWindow());
		}

		Color c_red = new Color(255, 0, 0);

		List<Sprite> sprites = new ArrayList<Sprite>();
		
		Border border = new Border(width, height);
		sprites.add(border);
		border.setColor(new Color(0, 128, 128).getRGB());
		
		Rect rect = new Rect(6, 6);
		sprites.add(rect);
		rect.setColor(c_red.getRGB());
		rect.setPos(width/2 - rect.getWidth()/2, height/2 - rect.getHeight()/2);
		
		StringTexturedSprite st = new StringTexturedSprite();
		sprites.add(st);
		st.setPos(2, 2);
		st.setTexture("    111111    \n"
					+ "   12222221   \n"
					+ "  1222222221  \n"
					+ "  1221221221  \n"
					+ " 122141141221 \n"
					+ " 121444444121 \n"
					+ " 114414414411 \n"
					+ " 114414414411 \n"
					+ " 121442244121 \n"
					+ " 112111111211 \n"
					+ "11313111131311\n"
					+ " 113333333311 \n"
					+ "  1333333331  \n"
					+ "   13333331   \n"
					+ "    111111    ");
		
		boolean running = true;
		while(running) {
			long t_start = System.nanoTime();
			fb.clear();
			sprites.forEach(sprite -> fb.blit(sprite));
			
			r.draw();
			rect.setColor(Util.rgbRainbowNext(rect.getRGB(0, 0)));
			
			if(in.hasKey()) {
				char k = in.getKey();
				switch(k) {
				case 'q': // quit
					r.close();
					running = false;
					break;
				case 'w': // move up
					rect.setRel(0, -1);
					break;
				case 's': // move down
					rect.setRel(0, 1);
					break;
				case 'a': // move left
					rect.setRel(-1, 0);
					break;
				case 'd': // move right
					rect.setRel(1, 0);
					break;
				}
			}
			
			if(running) {
				// wait for next frame time on 30Hz bounds
				long t_end = System.nanoTime();
				double t_delta = (t_end - t_start) / 1e6; // in millis
				long t_wait = (long) Math.max(0, 33.33 - t_delta);
				TimeUnit.MILLISECONDS.sleep(t_wait);
			}
		}
	}
}
