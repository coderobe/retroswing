package net.coderobe.retroswing.emu.gb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		byte[] cartridge = Files.readAllBytes(Paths.get(args[0]));
		
		Core core = new Core();
		
		// Power Up Sequence
		core.mmu.reg_8.put('A', (byte) 0x01); // GB
		core.mmu.reg_8.put('F', (byte) 0xB0);
		core.mmu.reg_16.put("BC", (short) 0x0013);
		core.mmu.reg_16.put("DE", (short) 0x00D8);
		core.mmu.reg_16.put("HL", (short) 0x014D);
		core.mmu.SP = (short) 0xFFFE;
		core.mmu.ram.put((short) 0xFF05, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF06, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF07, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF10, (byte) 0x80);
		core.mmu.ram.put((short) 0xFF11, (byte) 0xBF);
		core.mmu.ram.put((short) 0xFF12, (byte) 0xF3);
		core.mmu.ram.put((short) 0xFF14, (byte) 0xBF);
		core.mmu.ram.put((short) 0xFF16, (byte) 0x3F);
		core.mmu.ram.put((short) 0xFF17, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF19, (byte) 0xBF);
		core.mmu.ram.put((short) 0xFF1A, (byte) 0x7F);
		core.mmu.ram.put((short) 0xFF1B, (byte) 0xFF);
		core.mmu.ram.put((short) 0xFF1C, (byte) 0x9F);
		core.mmu.ram.put((short) 0xFF1E, (byte) 0xBF);
		core.mmu.ram.put((short) 0xFF20, (byte) 0xFF);
		core.mmu.ram.put((short) 0xFF21, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF22, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF23, (byte) 0xBF);
		core.mmu.ram.put((short) 0xFF24, (byte) 0x77);
		core.mmu.ram.put((short) 0xFF25, (byte) 0xF3);
		core.mmu.ram.put((short) 0xFF26, (byte) 0xF1); // GB
		core.mmu.ram.put((short) 0xFF40, (byte) 0x91);
		core.mmu.ram.put((short) 0xFF42, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF43, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF45, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF47, (byte) 0xFC);
		core.mmu.ram.put((short) 0xFF48, (byte) 0xFF);
		core.mmu.ram.put((short) 0xFF49, (byte) 0xFF);
		core.mmu.ram.put((short) 0xFF4A, (byte) 0x00);
		core.mmu.ram.put((short) 0xFF4B, (byte) 0x00);
		core.mmu.ram.put((short) 0xFFFF, (byte) 0x00);
		
		// Read cartridge
		String cart_name = "";
		for(int loc = 0x134; loc < 0x13F; loc++) {
			char cart_d = (char)cartridge[loc];
			if(cart_d == '\0') break;
			cart_name += cart_d;
		}
		System.out.println("Cartridge name: "+cart_name);
		String cart_code = "";
		for(int loc = 0x13F; loc < 0x143; loc++) {
			char cart_d = (char)cartridge[loc];
			if(cart_d == '\0') break;
			cart_code += cart_d;
		}
		System.out.println("Cartridge code: "+cart_code);
		System.out.println("Cartridge type: "+cartridge[0x147]);
		int mem_cart = 0x0000;
		for(byte b : cartridge) {
			core.mmu.ram.put((short) mem_cart++, b);
		}
		
		System.out.println("Starting core ticking");
		
		try {
			while(true) {
				core.tick();
			}
		} catch(UnknownOpcodeException e) {
			System.err.println(e.getMessage());
		}
	}
}
