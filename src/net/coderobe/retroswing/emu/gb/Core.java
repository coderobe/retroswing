package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	public Memory mmu = new Memory();
	public Video gpu = new Video(mmu);
	public boolean interruptable = true;
	public void tick() throws UnknownOpcodeException {
		mmu.ram.put((short) 0xFF44, (byte) (mmu.ram.get((short) 0xFF44)+1)); // scroll LY until we have lcd rendering (TODO)
		Opcode op = opcodes.get(mmu.ram.get(mmu.PC++));
		if(op == null) {
			throw new UnknownOpcodeException("Unknown opcode "+String.format("0x%02X", mmu.ram.get((short)(mmu.PC-1)))+" at address "+String.format("0x%04X", (short)(mmu.PC-1)));
		}
		op.exec();
	}
	private final Map<Byte, Opcode> opcodes = new HashMap<Byte, Opcode>(){{
		// NOP
		put((byte) 0x00, () -> {
			// hurr
		});
		// RLA
		put((byte) 0x17, () -> {
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', mmu.get_bit('C', 7));
			mmu.reg_8.put('A', (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A')) << 1));
			mmu.set_flag('Z', Byte.toUnsignedInt(mmu.reg_8.get('A')) == 0);
		});
		// RRA
		put((byte) 0x1F, () -> {
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', (Byte.toUnsignedInt(mmu.reg_8.get('A')) & 1) == 1);
			mmu.reg_8.put('A', (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A')) >> 1));
			mmu.set_flag('Z', Byte.toUnsignedInt(mmu.reg_8.get('A')) == 0);
		});
		// JR
		put((byte) 0x18, () -> {
			byte amount = mmu.ram.get(mmu.PC++); // signed!
			mmu.PC = (short) (Short.toUnsignedInt(mmu.PC) + amount);
		});
		// JR NZ,n
		put((byte) 0x20, () -> {
			byte amount = mmu.ram.get(mmu.PC++); // signed!
			if(!mmu.get_flag('Z')) {
				mmu.PC = (short) (Short.toUnsignedInt(mmu.PC) + amount);
			}
		});
		// JR Z,n
		put((byte) 0x28, () -> {
			byte amount = mmu.ram.get(mmu.PC++); // signed!
			if(mmu.get_flag('Z')) {
				mmu.PC = (short) (Short.toUnsignedInt(mmu.PC) + amount);
			}
		});
		// JR NC,n
		put((byte) 0x30, () -> {
			byte amount = mmu.ram.get(mmu.PC++); // signed!
			if(!mmu.get_flag('C')) {
				mmu.PC = (short) (Short.toUnsignedInt(mmu.PC) + amount);
			}
		});
		// JR C,n
		put((byte) 0x38, () -> {
			byte amount = mmu.ram.get(mmu.PC++); // signed!
			if(mmu.get_flag('C')) {
				mmu.PC = (short) (Short.toUnsignedInt(mmu.PC) + amount);
			}
		});
		// CPL
		put((byte) 0x2F, () -> {
			mmu.set_flag('N', true);
			mmu.set_flag('H', true);
			mmu.reg_8.put('A', (byte)~(Byte.toUnsignedInt(mmu.reg_8.get('A'))));
		});
		// LDI (HL),A
		put((byte) 0x22, () -> {
			short addr = mmu.reg_16.get("HL");
			mmu.ram.put(addr, mmu.reg_8.get('A'));
			mmu.reg_16.put("HL", (short)(Short.toUnsignedInt(addr)+1));
		});
		// LDD (HL),A
		put((byte) 0x32, () -> {
			short addr = mmu.reg_16.get("HL");
			mmu.ram.put(addr, mmu.reg_8.get('A'));
			mmu.reg_16.put("HL", (short)(Short.toUnsignedInt(addr)-1));
		});
		// SCF
		put((byte) 0x37, () -> {
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', true);
		});
		// CCF
		put((byte) 0x3F, () -> {
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', !mmu.get_flag('C'));
		});
		// ADD A,A
		put((byte) 0x87, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(a);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,B
		put((byte) 0x80, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('B'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,C
		put((byte) 0x81, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('C'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,D
		put((byte) 0x82, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('D'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,E
		put((byte) 0x83, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('E'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,H
		put((byte) 0x84, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('H'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADD A,L
		put((byte) 0x85, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('L'));
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,A
		put((byte) 0x8F, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(a) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,B
		put((byte) 0x88, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('B')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,C
		put((byte) 0x89, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('C')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,D
		put((byte) 0x8A, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('D')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,E
		put((byte) 0x8B, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('E')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,H
		put((byte) 0x8C, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('H')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// ADC A,L
		put((byte) 0x8D, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) + Short.toUnsignedInt(mmu.reg_8.get('L')) + (mmu.get_flag('C') ? 1 : 0);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // carry from bit 3
			mmu.set_flag('H', (sum & 0xFF) < (a & 0xFF)); // carry from bit 7
			mmu.set_flag('N', false);
		});
		// SUB A,A
		put((byte) 0x97, () -> {
			byte a = mmu.reg_8.get('A');
			int sum = Short.toUnsignedInt(a) - Short.toUnsignedInt(a);
			mmu.reg_8.put('A', (byte) sum);
			mmu.set_flag('C', sum > 0xF); // no borrow from bit 4 // TODO: double check
			mmu.set_flag('H', sum > 0); // no borrow
			mmu.set_flag('N', false);
		});
		// XOR A,C
		put((byte) 0xA9, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.reg_8.get('C');
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,D
		put((byte) 0xAA, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.reg_8.get('D');
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,E
		put((byte) 0xAB, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.reg_8.get('E');
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,H
		put((byte) 0xAC, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.reg_8.get('H');
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,L
		put((byte) 0xAD, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.reg_8.get('L');
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,(HL)
		put((byte) 0xAE, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.ram.get(mmu.reg_16.get("HL"));
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,*
		put((byte) 0xEE, () -> {
			byte r1 = mmu.reg_8.get('A');
			byte r2 = mmu.ram.get(mmu.PC++);
			byte r = (byte)(r2 ^ r1);
			mmu.reg_8.put('A', r);
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// XOR A,A
		put((byte) 0xAF, () -> {
			mmu.reg_8.put('A', (byte) 0x00);
			mmu.set_flag('Z', true);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// JP NZ,nn
		put((byte) 0xC2, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			if(!mmu.get_flag('Z')) {
				mmu.PC = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// JP nn
		put((byte) 0xC3, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.PC = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
		});
		// JP Z,nn
		put((byte) 0xCA, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			if(mmu.get_flag('Z')) {
				mmu.PC = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// JP NC,nn
		put((byte) 0xD2, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			if(!mmu.get_flag('C')) {
				mmu.PC = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// JP C,nn
		put((byte) 0xDA, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			if(mmu.get_flag('C')) {
				mmu.PC = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// JP (HL)
		put((byte) 0xE9, () -> {
			mmu.PC = mmu.reg_16.get("HL");
		});
		// POP AF
		put((byte) 0xF1, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.reg_16.put("AF", (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// POP BC
		put((byte) 0xC1, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.reg_16.put("BC", (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// POP DE
		put((byte) 0xD1, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.reg_16.put("DE", (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// POP HL
		put((byte) 0xE1, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.reg_16.put("HL", (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// RST 28H
		put((byte) 0xEF, () -> {
			mmu.ram.put(mmu.SP--, (byte) (Short.toUnsignedInt(mmu.PC) >> 8));
			mmu.ram.put(mmu.SP--, (byte) (Short.toUnsignedInt(mmu.PC) & 0xFF));
			mmu.PC = 0x28;
		});
		// RST 38H
		put((byte) 0xFF, () -> {
			mmu.ram.put(mmu.SP--, (byte) (Short.toUnsignedInt(mmu.PC) >> 8));
			mmu.ram.put(mmu.SP--, (byte) (Short.toUnsignedInt(mmu.PC) & 0xFF));
			mmu.PC = 0x38;
		});
		// INC BC
		put((byte) 0x03, () -> {
			int res = Short.toUnsignedInt(mmu.reg_16.get("BC")) + 1;
			mmu.reg_16.put("BC", (short) res);
		});
		// INC DE
		put((byte) 0x13, () -> {
			int res = Short.toUnsignedInt(mmu.reg_16.get("DE")) + 1;
			mmu.reg_16.put("DE", (short) res);
		});
		// INC HL
		put((byte) 0x23, () -> {
			int res = Short.toUnsignedInt(mmu.reg_16.get("HL")) + 1;
			mmu.reg_16.put("HL", (short) res);
		});
		// INC SP
		put((byte) 0x33, () -> {
			mmu.SP++;
		});
		// ADD HL,BC
		put((byte) 0x09, () -> {
			short hl = mmu.reg_16.get("HL");
			int sum = Short.toUnsignedInt(hl) + Short.toUnsignedInt(mmu.reg_16.get("BC"));
			mmu.reg_16.put("HL", (short) sum);
			mmu.set_flag('C', sum > 0xFFFF); // carry from bit 15
			mmu.set_flag('H', (sum & 0xFFF) < (hl & 0xFFF)); // carry from bit 11
			mmu.set_flag('N', false);
		});
		// ADD HL,DE
		put((byte) 0x19, () -> {
			short hl = mmu.reg_16.get("HL");
			int sum = Short.toUnsignedInt(hl) + Short.toUnsignedInt(mmu.reg_16.get("DE"));
			mmu.reg_16.put("HL", (short) sum);
			mmu.set_flag('C', sum > 0xFFFF); // carry from bit 15
			mmu.set_flag('H', (sum & 0xFFF) < (hl & 0xFFF)); // carry from bit 11
			mmu.set_flag('N', false);
		});
		// ADD HL,HL
		put((byte) 0x29, () -> {
			short hl = mmu.reg_16.get("HL");
			int sum = Short.toUnsignedInt(hl) + Short.toUnsignedInt(hl);
			mmu.reg_16.put("HL", (short) sum);
			mmu.set_flag('C', sum > 0xFFFF); // carry from bit 15
			mmu.set_flag('H', (sum & 0xFFF) < (hl & 0xFFF)); // carry from bit 11
			mmu.set_flag('N', false);
		});
		// ADD HL,SP
		put((byte) 0x39, () -> {
			short hl = mmu.reg_16.get("HL");
			int sum = Short.toUnsignedInt(hl) + Short.toUnsignedInt(mmu.SP);
			mmu.reg_16.put("HL", (short) sum);
			mmu.set_flag('C', sum > 0xFFFF); // carry from bit 15
			mmu.set_flag('H', (sum & 0xFFF) < (hl & 0xFFF)); // carry from bit 11
			mmu.set_flag('N', false);
		});
		// DEC A
		put((byte) 0x3D, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('A')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('A', (byte) res);
		});
		// DEC B
		put((byte) 0x05, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('B')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('B', (byte) res);
		});
		// DEC C
		put((byte) 0x0D, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('C')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('C', (byte) res);
		});
		// DEC D
		put((byte) 0x15, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('D')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('D', (byte) res);
		});
		// DEC E
		put((byte) 0x1D, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('E')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('E', (byte) res);
		});
		// DEC H
		put((byte) 0x25, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('H')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('H', (byte) res);
		});
		// DEC L
		put((byte) 0x2D, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('L')) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.reg_8.put('L', (byte) res);
		});
		// DEC (HL)
		put((byte) 0x35, () -> {
			int res = Byte.toUnsignedInt(mmu.ram.get(mmu.reg_16.get("HL"))) - 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0xF);
			mmu.ram.put(mmu.reg_16.get("HL"), (byte) res);
		});
		// LD HL, nn
		put((byte) 0x21, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.reg_16.put("HL", (short) (Byte.toUnsignedInt(upper) << 8 | Byte.toUnsignedInt(lower)));
		});
		// LD B, n
		put((byte) 0x06, () -> {
			mmu.reg_8.put('B', mmu.ram.get(mmu.PC++));
		});
		// LD C, n
		put((byte) 0x0E, () -> {
			mmu.reg_8.put('C', mmu.ram.get(mmu.PC++));
		});
		// LD D, n
		put((byte) 0x16, () -> {
			mmu.reg_8.put('D', mmu.ram.get(mmu.PC++));
		});
		// LD E, n
		put((byte) 0x1E, () -> {
			mmu.reg_8.put('E', mmu.ram.get(mmu.PC++));
		});
		// LD H, n
		put((byte) 0x26, () -> {
			mmu.reg_8.put('H', mmu.ram.get(mmu.PC++));
		});
		// LD L, n
		put((byte) 0x2E, () -> {
			mmu.reg_8.put('L', mmu.ram.get(mmu.PC++));
		});
		// LD A,#
		put((byte) 0x3E, () -> {
			mmu.reg_8.put('A', mmu.ram.get(mmu.PC++));
		});
		// LD A,(C)
		put((byte) 0xF2, () -> {
			mmu.reg_8.put('A', mmu.ram.get((short)(0xFF00 + mmu.reg_8.get('C'))));
		});
		// LDH (n),A
		put((byte) 0xE0, () -> {
			short addr = (short) (0xFF00 + Byte.toUnsignedInt(mmu.ram.get(mmu.PC++)));
			mmu.ram.put(addr, mmu.reg_8.get('A'));
		});
		// LDH A,(n)
		put((byte) 0xF0, () -> {
			short addr = (short) (0xFF00 + Byte.toUnsignedInt(mmu.ram.get(mmu.PC++)));
			mmu.reg_8.put('A', mmu.ram.get(addr));
		});
		// DI
		put((byte) 0xF3, () -> {
			// TODO: fix interrupts
			interruptable = false;
		});
		// EI
		put((byte) 0xFB, () -> {
			// TODO: fix interrupts
			interruptable = true;
		});
		// CP H
		put((byte) 0xBC, () -> {
			byte a = mmu.reg_8.get('A');
			byte res = (byte) (Byte.toUnsignedInt(a) - Byte.toUnsignedInt(mmu.reg_8.get('H')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.set_flag('C', false);
		});
		// CP A
		put((byte) 0xBF, () -> {
			byte a = mmu.reg_8.get('A');
			byte res = (byte) (Byte.toUnsignedInt(a) - Byte.toUnsignedInt(a));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.set_flag('C', false);
		});
		// CP #
		put((byte) 0xFE, () -> {
			byte a = mmu.reg_8.get('A');
			byte n = mmu.ram.get(mmu.PC++);
			byte res = (byte) (Byte.toUnsignedInt(a) - Byte.toUnsignedInt(n));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.set_flag('C', a < n);
		});
		// LD r1, r2
		// LD A ,A
		put((byte) 0x7F, () -> {
			// Do nothing, as A is already inside A.
		});
		// LD A ,B
		put((byte) 0x78, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('B'));
		});
		// LD A, C
		put((byte) 0x79, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('C'));
		});
		// LD A, D
		put((byte) 0x7A, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('D'));
		});
		// LD A, E
		put((byte) 0x7B, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('E'));
		});
		// LD A, H
		put((byte) 0x7C, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('H'));
		});
		// LD A, L
		put((byte) 0x7D, () -> {
			mmu.reg_8.put('A', mmu.reg_8.get('L'));
		});
		// LD A, (HL)
		put((byte) 0x7E, () -> {
			mmu.reg_8.put('A', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD B, B
		put((byte) 0x40, () -> {
			// Do nothing as B is already in B.
		});
		// LD B, C
		put((byte) 0x41, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('C'));
		});
		// LD B, D
		put((byte) 0x42, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('D'));
		});
		// LD B, E
		put((byte) 0x43, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('E'));
		});
		// LD B, H
		put((byte) 0x44, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('H'));
		});
		// LD B, L
		put((byte) 0x45, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('L'));
		});
		// LD B, (HL)
		put((byte) 0x46, () -> {
			mmu.reg_8.put('B', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD C, B
		put((byte) 0x48, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('B'));
		});
		// LD C, C
		put((byte) 0x49, () -> {
			// Do nothing as C is already in C.
		});
		// LD C, D
		put((byte) 0x4A, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('D'));
		});
		// LD C, E
		put((byte) 0x4B, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('E'));
		});
		// LD C, H
		put((byte) 0x4C, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('H'));
		});
		// LD C, L
		put((byte) 0x4D, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('L'));
		});
		// LD C, (HL)
		put((byte) 0x4E, () -> {
			mmu.reg_8.put('C', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD D, B
		put((byte) 0x50, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('B'));
		});
		// LD D, C
		put((byte) 0x51, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('C'));
		});
		// LD D, D
		put((byte) 0x52, () -> {
			// Do nothing as D is already in D.
		});
		// LD D, E
		put((byte) 0x53, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('E'));
		});
		// LD D, H
		put((byte) 0x54, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('H'));
		});
		// LD D, L
		put((byte) 0x55, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('L'));
		});
		// LD D, (HL)
		put((byte) 0x56, () -> {
			mmu.reg_8.put('D', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD E, B
		put((byte) 0x58, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('B'));
		});
		// LD E, C
		put((byte) 0x59, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('C'));
		});
		// LD E, H
		put((byte) 0x5A, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('D'));
		});
		// LD E, E
		put((byte) 0x5B, () -> {
			// Do nothing as E is already in E.
		});
		// LD E, H
		put((byte) 0x5C, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('H'));
		});
		// LD E, L
		put((byte) 0x5D, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('L'));
		});
		// LD E, (HL)
		put((byte) 0x5E, () -> {
			mmu.reg_8.put('E', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD H, B
		put((byte) 0x60, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('B'));
		});
		// LD H, C
		put((byte) 0x61, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('C'));
		});
		// LD H, D
		put((byte) 0x62, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('D'));
		});
		// LD H, E
		put((byte) 0x63, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('E'));
		});
		// LD H, H
		put((byte) 0x64, () -> {
			// Do nothing as H is already in H.
		});
		// LD H, L
		put((byte) 0x65, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('L'));
		});
		// LD H, (HL)
		put((byte) 0x66, () -> {
			mmu.reg_8.put('H', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD L, B
		put((byte) 0x68, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('B'));
		});
		// LD L, C
		put((byte) 0x69, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('C'));
		});
		// LD L, D
		put((byte) 0x6A, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('D'));
		});
		// LD L, E
		put((byte) 0x6B, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('E'));
		});
		// LD L, H
		put((byte) 0x6C, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('H'));
		});
		// LD L, L
		put((byte) 0x6D, () -> {
			// Do nothing as L is already in L.
		});
		// LD L, (HL)
		put((byte) 0x6E, () -> {
			mmu.reg_8.put('L', mmu.ram.get(mmu.reg_16.get("HL")));
		});
		// LD (HL), B
		put((byte) 0x70, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('B'));
		});
		// LD (HL), C
		put((byte) 0x71, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('C'));
		});
		// LD (HL), D
		put((byte) 0x72, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('D'));
		});
		// LD (HL), E
		put((byte) 0x73, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('E'));
		});
		// LD (HL), H
		put((byte) 0x74, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('H'));
		});
		// LD (HL), L
		put((byte) 0x75, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('L'));
		});
		// LD (HL), n
		put((byte) 0x36, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.ram.get(mmu.PC++));
		});
		// LD A, n (additional)
		// A, (BC)
		put((byte) 0x0A, () -> {
			mmu.reg_8.put('A', mmu.ram.get(mmu.reg_16.get("BC")));
		});
		// A, (DE)
		put((byte) 0x1A, () -> {
			mmu.reg_8.put('A', mmu.ram.get(mmu.reg_16.get("DE")));
		});
		// A, (nn)
		put((byte) 0xFA, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.reg_8.put('A', mmu.ram.get((short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8))));
		});
		// LD n, A
		// LD B, A
		put((byte) 0x47, () -> {
			mmu.reg_8.put('B', mmu.reg_8.get('A'));
		});
		// LD C, A
		put((byte) 0x4F, () -> {
			mmu.reg_8.put('C', mmu.reg_8.get('A'));
		});
		// LD D, A
		put((byte) 0x57, () -> {
			mmu.reg_8.put('D', mmu.reg_8.get('A'));
		});
		// LD E, A
		put((byte) 0x5F, () -> {
			mmu.reg_8.put('E', mmu.reg_8.get('A'));
		});
		// LD H, A
		put((byte) 0x67, () -> {
			mmu.reg_8.put('H', mmu.reg_8.get('A'));
		});
		// LD L, A
		put((byte) 0x6F, () -> {
			mmu.reg_8.put('L', mmu.reg_8.get('A'));
		});
		// LD (BC), A
		put((byte) 0x02, () -> {
			mmu.ram.put(mmu.reg_16.get("BC"), mmu.reg_8.get('A'));
		});
		// LD (DE), A
		put((byte) 0x12, () -> {
			mmu.ram.put(mmu.reg_16.get("DE"), mmu.reg_8.get('A'));
		});
		// LD (HL), A
		put((byte) 0x77, () -> {
			mmu.ram.put(mmu.reg_16.get("HL"), mmu.reg_8.get('A'));
		});
		// LD (nn), A
		put((byte) 0xEA, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.ram.put((short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)), mmu.reg_8.get('A'));
		});
		// LD n, nn (16-bit loads)
		// LD BC, nn
		put((byte) 0x01, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.reg_16.put("BC", (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// LD DE, nn
		put((byte) 0x11, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.reg_16.put("DE", (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8)));
		});
		// LD SP, nn
		put((byte) 0x31, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.SP = (short)(Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
		});
		// LDI A, (HL)
		put((byte) 0x2A, () -> {
			mmu.reg_8.put('A', mmu.ram.get(mmu.reg_16.get("HL")));
			mmu.reg_16.put("HL", (short)(Short.toUnsignedInt(mmu.reg_16.get("HL")) + 1));
		});
		// LD (C), A
		put((byte) 0xE2, () -> {
			mmu.ram.put((short)(0xFF00 + Byte.toUnsignedInt(mmu.reg_8.get('C'))), mmu.reg_8.get('A'));
		});
		// INC n
		// INC A
		put((byte) 0x3C, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('A')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('A', (byte) res);
		});
		// INC B
		put((byte) 0x04, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('B')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('B', (byte) res);
		});
		// INC C
		put((byte) 0x0C, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('C')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('C', (byte) res);
		});
		// INC D
		put((byte) 0x14, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('D')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('D', (byte) res);
		});
		// INC E
		put((byte) 0x1C, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('E')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('E', (byte) res);
		});
		// INC H
		put((byte) 0x24, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('H')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('H', (byte) res);
		});
		// INC L
		put((byte) 0x2C, () -> {
			int res = Byte.toUnsignedInt(mmu.reg_8.get('L')) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('L', (byte) res);
		});
		// INC (HL)
		put((byte) 0x34, () -> {
			int res = Byte.toUnsignedInt(mmu.ram.get(mmu.reg_16.get("HL"))) + 1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.ram.put(mmu.reg_16.get("HL"), (byte) res);
		});
		// PUSH AF
		put((byte) 0xF5, () -> {
			byte upper = mmu.reg_8.get('A');
			byte lower = mmu.reg_8.get('F');
			mmu.ram.put(mmu.SP--, upper);
			mmu.ram.put(mmu.SP--, lower);
		});
		// PUSH BC
		put((byte) 0xC5, () -> {
			byte upper = mmu.reg_8.get('B');
			byte lower = mmu.reg_8.get('C');
			mmu.ram.put(mmu.SP--, upper);
			mmu.ram.put(mmu.SP--, lower);
		});
		// PUSH DE
		put((byte) 0xD5, () -> {
			byte upper = mmu.reg_8.get('D');
			byte lower = mmu.reg_8.get('E');
			mmu.ram.put(mmu.SP--, upper);
			mmu.ram.put(mmu.SP--, lower);
		});
		// PUSH HL
		put((byte) 0xE5, () -> {
			byte upper = mmu.reg_8.get('H');
			byte lower = mmu.reg_8.get('L');
			mmu.ram.put(mmu.SP--, upper);
			mmu.ram.put(mmu.SP--, lower);
		});
		// CALL NZ,nn
		put((byte) 0xC4, () -> {
			byte lower_call = mmu.ram.get(mmu.PC++);
			byte upper_call = mmu.ram.get(mmu.PC++);
			if(!mmu.get_flag('Z')) {
				byte lower = (byte)mmu.PC;
				byte upper = (byte)(mmu.PC>>8);
				mmu.ram.put(mmu.SP--, upper);
				mmu.ram.put(mmu.SP--, lower);
				mmu.PC = (short) (Byte.toUnsignedInt(lower_call) | (Byte.toUnsignedInt(upper_call) << 8));
			}
		});
		// CALL
		put((byte) 0xCD, () -> {
			byte lower_call = mmu.ram.get(mmu.PC++);
			byte upper_call = mmu.ram.get(mmu.PC++);
			byte lower = (byte)mmu.PC;
			byte upper = (byte)(mmu.PC>>8);
			mmu.ram.put(mmu.SP--, upper);
			mmu.ram.put(mmu.SP--, lower);
			mmu.PC = (short) (Byte.toUnsignedInt(lower_call) | (Byte.toUnsignedInt(upper_call) << 8));
		});
		// RET NZ
		put((byte) 0xC0, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			if(!mmu.get_flag('Z')) {
				mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// RET Z
		put((byte) 0xC8, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			if(mmu.get_flag('Z')) {
				mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// RET NC
		put((byte) 0xD0, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			if(!mmu.get_flag('C')) {
				mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// RET C
		put((byte) 0xD8, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			if(mmu.get_flag('C')) {
				mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			}
		});
		// RET
		put((byte) 0xC9, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
		});
		// RETI
		put((byte) 0xD9, () -> {
			byte lower = mmu.ram.get(++mmu.SP);
			byte upper = mmu.ram.get(++mmu.SP);
			mmu.PC = (short) (Byte.toUnsignedInt(lower) | (Byte.toUnsignedInt(upper) << 8));
			// TODO: interrupts
		});
		// DEC nn (16-bit)
		// DEC BC
		put((byte) 0x0B, () -> {
			mmu.reg_16.put("BC", (short)(Short.toUnsignedInt(mmu.reg_16.get("BC")) - 1));
		});
		// DEC DE
		put((byte) 0x1B, () -> {
			mmu.reg_16.put("DE", (short)(Short.toUnsignedInt(mmu.reg_16.get("DE")) - 1));
		});
		// DEC HL
		put((byte) 0x2B, () -> {
			mmu.reg_16.put("HL", (short)(Short.toUnsignedInt(mmu.reg_16.get("HL")) - 1));
		});
		// DEC SP
		put((byte) 0x3B, () -> {
			mmu.SP = (short)(Short.toUnsignedInt(mmu.SP) - 1);
		});
		// OR n (8-bit ALU)
		// OR A
		put((byte) 0xB7, () -> {
			if (mmu.reg_8.get('A') == 0) mmu.set_flag('Z', true);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// OR B
		put((byte) 0xB0, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('B')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR C
		put((byte) 0xB1, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('C')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR D
		put((byte) 0xB2, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('D')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR E
		put((byte) 0xB3, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('E')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR H
		put((byte) 0xB4, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('H')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR L
		put((byte) 0xB5, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.reg_8.get('L')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR (HL)
		put((byte) 0xB6, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.ram.get(mmu.reg_16.get("HL"))));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// OR #
		put((byte) 0xF6, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					| Byte.toUnsignedInt(mmu.ram.get(mmu.PC++)));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// AND C
		put((byte) 0xA1, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					& Byte.toUnsignedInt(mmu.reg_8.get('C')));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', true);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// AND (HL)
		put((byte) 0xA6, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					& Byte.toUnsignedInt(mmu.ram.get(mmu.reg_16.get("HL"))));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', true);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// AND A
		put((byte) 0xA7, () -> {
			mmu.set_flag('Z', mmu.reg_8.get('A') == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', true);
			mmu.set_flag('C', false);
		});
		// AND #
		put((byte) 0xE6, () -> {
			byte res = (byte)(Byte.toUnsignedInt(mmu.reg_8.get('A'))
					& Byte.toUnsignedInt(mmu.ram.get(mmu.PC++)));
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', true);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', res);
		});
		// SBC A,E
		put((byte) 0x9B, () -> {
			byte a = mmu.reg_8.get('A');
			byte e = mmu.reg_8.get('E');
			int res = a - Byte.toUnsignedInt(e);
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) < (a & 0xF));
			mmu.set_flag('C', res < 0);
			mmu.reg_8.put('A', (byte) res);
		});
		// SBC A,A
		put((byte) 0x9F, () -> { // TODO: optimize
			byte a = mmu.reg_8.get('A');
			int res = a - Byte.toUnsignedInt(a);
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) < (a & 0xF));
			mmu.set_flag('C', res < 0);
			mmu.reg_8.put('A', (byte) res);
		});
		// long opcode
		put((byte) 0xCB, () -> {
			Opcode op = cb_opcodes.get(mmu.ram.get(mmu.PC++));
			if(op == null) {
				throw new UnknownOpcodeException("Unknown opcode 0xCB "+String.format("0x%02X", mmu.ram.get((short)(mmu.PC-1)))+" at address "+String.format("0x%04X", (short)(mmu.PC-1)));
			}
			op.exec();
		});
	}};
	private final Map<Byte, Opcode> cb_opcodes = new HashMap<Byte, Opcode>(){{
		// SWAP A
		put((byte) 0x37, () -> {
			byte r = mmu.reg_8.get('A');
			r = (byte)((r << 4) | (r >> 4));
			mmu.set_flag('Z', r == 0);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
			mmu.reg_8.put('A', r);
		});
		// RES 0,A
		put((byte) 0x87, () -> {
			mmu.set_bit('A', 0, false);
		});
		// SET 1,(HL)
		put((byte) 0xCE, () -> {
			mmu.set_bit(mmu.reg_16.get("HL"), 1, true);
		});
	}};
}
