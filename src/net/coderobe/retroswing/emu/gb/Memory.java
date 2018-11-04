package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Memory {
	public final Map<String, Short> reg_io_loc = new HashMap<String, Short>(){{
		// joy pad
		put("P1", (short) 0xFF00);
		// serial binary transfer
		put("SB", (short) 0xFF01);
		// serial control
		put("SC", (short) 0xFF02);
		// divider
		put("DIV", (short) 0xFF04);
		// timer counter
		put("TIMA", (short) 0xFF05);
		// timer modulo
		put("TMA", (short) 0xFF06);
		// timer control
		put("TAC", (short) 0xFF07);
		// interrupt flag
		put("IF", (short) 0xFF0F);
		// sound
		put("NR10", (short) 0xFF10);
		put("NR11", (short) 0xFF11);
		put("NR12", (short) 0xFF12);
		put("NR13", (short) 0xFF13);
		put("NR14", (short) 0xFF14);
		put("NR21", (short) 0xFF16);
		put("NR22", (short) 0xFF17);
		put("NR23", (short) 0xFF18);
		put("NR24", (short) 0xFF19);
		put("NR30", (short) 0xFF1A);
		put("NR31", (short) 0xFF1B);
		put("NR32", (short) 0xFF1C);
		put("NR33", (short) 0xFF1D);
		put("NR34", (short) 0xFF1E);
		put("NR41", (short) 0xFF20);
		put("NR42", (short) 0xFF21);
		put("NR43", (short) 0xFF22);
		put("NR44", (short) 0xFF23);
		put("NR50", (short) 0xFF24);
		put("NR51", (short) 0xFF25);
		put("NR52", (short) 0xFF26);
		// lcd
		put("LCDC", (short) 0xFF40);
		put("STAT", (short) 0xFF41);
		put("SCY", (short) 0xFF42);
		put("SCX", (short) 0xFF43);
		put("LY", (short) 0xFF44);
		put("LYC", (short) 0xFF45);
		put("DMA", (short) 0xFF46);
		put("BGP", (short) 0xFF47);
		put("OBP0", (short) 0xFF48);
		put("OBP1", (short) 0xFF49);
		put("WY", (short) 0xFF4A);
		put("WX", (short) 0xFF4B);
		// interrupt enable
		put("IE", (short) 0xFFFF);
	}};
	public final Map<String, Byte> reg_io = new HashMap<String, Byte>(){
		@Override
		public Byte get(Object s_o) {
			return ram.get(reg_io_loc.get((String) s_o));
		}
		@Override
		public Byte put(String s, Byte b) {
			ram.put(reg_io_loc.get(s), b);
			return b;
		}
	};
	// 8-bit registers
	public final Map<Character, Byte> reg_8 = new HashMap<Character, Byte>(){{
		put('A', (byte) 0x00);
		put('F', (byte) 0x00);

		put('B', (byte) 0x00);
		put('C', (byte) 0x00);

		put('D', (byte) 0x00);
		put('E', (byte) 0x00);

		put('H', (byte) 0x00);
		put('L', (byte) 0x00);
	}};
	public void set_flag(char flag, boolean state) {
		byte f = reg_8.get('F');
		byte mask = 1;
		switch(flag) {
		case 'Z':
			mask <<= 1;
		case 'N':
			mask <<= 1;
		case 'H':
			mask <<= 1;
		case 'C':
			mask <<= 1;
		break;
		default:
			return;
		}
		mask <<= 3;
		if(state) {
			reg_8.put('F', (byte) (f | mask));
		} else {
			reg_8.put('F', (byte) (f & ~mask));
		}
	}
	public boolean get_flag(char flag) {
		byte f = reg_8.get('F');
		byte mask = 1;
		switch(flag) {
		case 'Z':
			mask <<= 1;
		case 'N':
			mask <<= 1;
		case 'H':
			mask <<= 1;
		case 'C':
			mask <<= 1;
		break;
		default:
			return false;
		}
		mask <<= 3;
		return (f & mask) != 0;
	}
	public short SP = (short) 0xFFFE; // Stack Pointer
	public short PC = 0x100; // Program Counter
	// 16-bit register access hack
	public final Map<String, Short> reg_16 = new HashMap<String, Short>(){
		@Override
		public Short get(Object r) {
			if(r.equals("SP")) {
				return SP;
			} else if(r.equals("PC")) {	
				return PC;
			} else {
				short ret = 0x0000;
				ret |= reg_8.get(((String) r).charAt(0));
				ret <<= 8;
				ret |= reg_8.get(((String) r).charAt(1));
				return ret;
			}
		}
		@Override
		public Short put(String r, Short v) {
			if(r.equals("SP")) {
				SP = v;
			} else if(r.equals("PC")) {
				PC = v;
			} else {
				reg_8.put(r.charAt(0), (byte) (v >> 8));
				reg_8.put(r.charAt(1), (byte) (v & 0xFF));
			}
			return v;
		}
	};
	// memory map
	public final Map<Short, Byte> ram = new HashMap<Short, Byte>(){
		@Override
		public Byte get(Object s_o) {
			Short s = (Short) s_o;
			int si = s << 1 >> 1;
			if(si >= 0xC000 && si <= 0xDE00) {
				return get((short) (si + 0x1000));
			} else {
				Byte r = super.get(s_o);
				if(r == null) r = 0x00;
				return r;
			}
		}
		@Override
		public Byte put(Short s, Byte b) {
			int si = s << 1 >> 1;
			if(si >= 0xC000 && si <= 0xDE00) {
				put((short) (si + 0x1000), b);
			} else {
				super.put(s, b);
			}
			return b;
		}
	};
}
