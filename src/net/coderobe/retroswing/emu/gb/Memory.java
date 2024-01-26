package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Memory {
	public final Map<String, Short> reg_io_loc = new ConcurrentHashMap<>(64, 1f, 1);
	public final Map<Character, Byte> reg_8 = new ConcurrentHashMap<>(8, 1f, 1);
	public final Map<String, Byte> reg_io = new ConcurrentHashMap<>();

	public Memory() {
		reg_io_loc.put("P1", (short) 0xFF00);  // joy pad
		reg_io_loc.put("SB", (short) 0xFF01);  // serial binary transfer
		reg_io_loc.put("SC", (short) 0xFF02);  // serial control
		reg_io_loc.put("DIV", (short) 0xFF04); // divider
		reg_io_loc.put("TIMA", (short) 0xFF05); // timer counter
		reg_io_loc.put("TMA", (short) 0xFF06);  // timer modulo
		reg_io_loc.put("TAC", (short) 0xFF07);  // timer control
		reg_io_loc.put("IF", (short) 0xFF0F);   // interrupt flag
		// Sound registers
		reg_io_loc.put("NR10", (short) 0xFF10);
		reg_io_loc.put("NR11", (short) 0xFF11);
		reg_io_loc.put("NR12", (short) 0xFF12);
		reg_io_loc.put("NR13", (short) 0xFF13);
		reg_io_loc.put("NR14", (short) 0xFF14);
		reg_io_loc.put("NR21", (short) 0xFF16);
		reg_io_loc.put("NR22", (short) 0xFF17);
		reg_io_loc.put("NR23", (short) 0xFF18);
		reg_io_loc.put("NR24", (short) 0xFF19);
		reg_io_loc.put("NR30", (short) 0xFF1A);
		reg_io_loc.put("NR31", (short) 0xFF1B);
		reg_io_loc.put("NR32", (short) 0xFF1C);
		reg_io_loc.put("NR33", (short) 0xFF1D);
		reg_io_loc.put("NR34", (short) 0xFF1E);
		reg_io_loc.put("NR41", (short) 0xFF20);
		reg_io_loc.put("NR42", (short) 0xFF21);
		reg_io_loc.put("NR43", (short) 0xFF22);
		reg_io_loc.put("NR44", (short) 0xFF23);
		reg_io_loc.put("NR50", (short) 0xFF24);
		reg_io_loc.put("NR51", (short) 0xFF25);
		reg_io_loc.put("NR52", (short) 0xFF26);
		// LCD registers
		reg_io_loc.put("LCDC", (short) 0xFF40);
		reg_io_loc.put("STAT", (short) 0xFF41);
		reg_io_loc.put("SCY", (short) 0xFF42);
		reg_io_loc.put("SCX", (short) 0xFF43);
		reg_io_loc.put("LY", (short) 0xFF44);
		reg_io_loc.put("LYC", (short) 0xFF45);
		reg_io_loc.put("DMA", (short) 0xFF46);
		reg_io_loc.put("BGP", (short) 0xFF47);
		reg_io_loc.put("OBP0", (short) 0xFF48);
		reg_io_loc.put("OBP1", (short) 0xFF49);
		reg_io_loc.put("WY", (short) 0xFF4A);
		reg_io_loc.put("WX", (short) 0xFF4B);
		// Interrupt Enable Register
		reg_io_loc.put("IE", (short) 0xFFFF);

		reg_8.put('A', (byte) 0x00);
		reg_8.put('F', (byte) 0x00);
		reg_8.put('B', (byte) 0x00);
		reg_8.put('C', (byte) 0x00);
		reg_8.put('D', (byte) 0x00);
		reg_8.put('E', (byte) 0x00);
		reg_8.put('H', (byte) 0x00);
		reg_8.put('L', (byte) 0x00);
	}

	public synchronized void set_flag(char flag, boolean state) {
		byte f = reg_8.get('F');
		byte mask = (byte)(1 << (flag - 'A'));
		if (state) {
			reg_8.put('F', (byte) (f | mask));
		} else {
			reg_8.put('F', (byte) (f & ~mask));
		}
	}

	public synchronized void set_bit(short addr, int bit, boolean state) {
		byte f = ram.get(addr);
		byte mask = (byte)(1 << bit);
		ram.put(addr, state ? (byte) (f | mask) : (byte) (f & ~mask));
	}

	public synchronized void set_bit(char reg, int bit, boolean state) {
		byte f = reg_8.get(reg);
		byte mask = (byte)(1 << bit);
		reg_8.put(reg, state ? (byte) (f | mask) : (byte) (f & ~mask));
	}

	public boolean get_bit(short addr, int bit) {
		return ((ram.get(addr) >>> bit) & 1) == 1;
	}

	public boolean get_bit(char reg, int bit) {
		return ((reg_8.get(reg) >>> bit) & 1) == 1;
	}

	public boolean get_flag(char flag) {
		byte f = reg_8.get('F');
		byte mask = (byte)(1 << (flag - 'A'));
		return (f & mask) != 0;
	}

	public volatile short SP = (short) 0xFFFE; // Stack Pointer
	public volatile short PC = 0x100; // Program Counter

	public final Map<String, Short> reg_16 = new ConcurrentHashMap<String, Short>() {
		@Override
		public Short get(Object r) {
			String key = (String) r;
			if ("SP".equals(key)) {
				return SP;
			} else if ("PC".equals(key)) {
				return PC;
			} else {
				short high = (short) Byte.toUnsignedInt(reg_8.get(key.charAt(0)));
				short low = (short) Byte.toUnsignedInt(reg_8.get(key.charAt(1)));
				return (short) (high << 8 | low);
			}
		}

		@Override
		public Short put(String r, Short v) {
			if ("SP".equals(r)) {
				SP = v;
			} else if ("PC".equals(r)) {
				PC = v;
			} else {
				reg_8.put(r.charAt(0), (byte) (v >>> 8));
				reg_8.put(r.charAt(1), (byte) (v & 0xFF));
			}
			return v;
		}
	};

	public final Map<Short, Byte> ram = new ConcurrentHashMap<Short, Byte>(0xFFFF, 1f, 1) {
		@Override
		public Byte get(Object s_o) {
			Short s = (Short) s_o;
			int si = s << 1 >>> 1;
			if (si >= 0xC000 && si <= 0xDE00) {
				return get((short) (si + 0x1000));
			} else {
				Byte r = super.get(s_o);
				return r != null ? r : 0x00;
			}
		}

		@Override
		public Byte put(Short s, Byte b) {
			int si = s << 1 >>> 1;
			if (si >= 0xC000 && si <= 0xDE00) {
				put((short) (si + 0x1000), b);
			} else {
				super.put(s, b);
			}
			return b;
		}
	};
}
