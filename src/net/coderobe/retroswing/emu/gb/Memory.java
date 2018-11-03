package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Memory {
	// 8-bit registers
	public final Map<String, Byte> reg_8 = new HashMap<String, Byte>(){{
		put("A", (byte) 0x00);
		put("F", (byte) 0x00);

		put("B", (byte) 0x00);
		put("C", (byte) 0x00);

		put("D", (byte) 0x00);
		put("E", (byte) 0x00);

		put("H", (byte) 0x00);
		put("L", (byte) 0x00);
	}};
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
				ret |= reg_8.get(String.valueOf(((String) r).charAt(0)));
				ret <<= 8;
				ret |= reg_8.get(String.valueOf(((String) r).charAt(1)));
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
				reg_8.put(String.valueOf(r.charAt(0)), (byte) (v >> 8));
				reg_8.put(String.valueOf(r.charAt(1)), (byte) (v & 0xFF));
			}
			return v;
		}
	};
	// memory map
	public final Map<Short, Byte> mem = new HashMap<Short, Byte>(){
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
