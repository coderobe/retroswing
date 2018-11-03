package net.coderobe.retroswing.emu.gb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		byte[] cartridge = Files.readAllBytes(Paths.get(args[0]));
		
		Core core = new Core();
		
		// Read cartridge into memory
		int mem_cart = 0x0000;
		for (byte b : cartridge) {
			core.mmu.ram.put((short) mem_cart++, b);
		}
		
		try {
			while(true) {
				core.tick();
			}
		} catch(UnknownOpcodeException e) {
			System.err.println(e.getMessage());
		}
	}
}
