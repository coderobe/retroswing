package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	public Memory mmu = new Memory();
	public boolean interruptable = true;
	public void tick() throws UnknownOpcodeException {
		try {
			opcodes.get(mmu.ram.get(mmu.PC++)).exec();
		} catch(NullPointerException e) {
			throw new UnknownOpcodeException("Unknown opcode "+String.format("0x%02X", mmu.ram.get((short)(mmu.PC-1)))+" at address "+String.format("0x%04X", (short)(mmu.PC-1)));
		}
	}
	private final Map<Byte, Opcode> opcodes = new HashMap<Byte, Opcode>(){{
		// NOP
		put((byte) 0x00, () -> {
			// hurr
		});
		// JR NZ,n
		put((byte) 0x20, () -> {
			byte amount = mmu.ram.get(mmu.PC++);
			if(!mmu.get_flag('Z')) {
				mmu.PC += amount;
			}
		});
		// LDD (HL),A
		put((byte) 0x32, () -> {
			short addr = mmu.reg_16.get("HL");
			mmu.ram.put(addr, mmu.reg_8.get('A'));
			mmu.reg_16.put("HL", (short)(addr-1));
		});
		// XOR A,A
		put((byte) 0xAF, () -> {
			mmu.reg_8.put('A', (byte) 0x00);
			mmu.set_flag('Z', true);
			mmu.set_flag('N', false);
			mmu.set_flag('H', false);
			mmu.set_flag('C', false);
		});
		// JP nn
		put((byte) 0xC3, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.PC = (short)((upper << 8) + lower);
		});
		// RST 38H
		put((byte) 0xFF, () -> {
			mmu.ram.put(mmu.SP--, (byte) (mmu.PC >> 8));
			mmu.ram.put(mmu.SP--, (byte) (mmu.PC & 0xFF));
			mmu.PC = 0x38;
		});
		// ADD HL,n
		put((byte) 0x39, () -> {
			short hl = mmu.reg_16.get("HL");
			int sum = hl + mmu.SP;
			mmu.reg_16.put("HL", (short) sum);
			mmu.set_flag('C', sum > 0xFFFF); // carry from bit 15
			mmu.set_flag('H', (sum & 0xFFF) < (hl & 0xFFF)); // carry from bit 11
			mmu.set_flag('N', false);
		});
		// DEC B
		put((byte) 0x05, () -> {
			int res = mmu.reg_8.get('B')-1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('B', (byte) res);
		});
		// DEC C
		put((byte) 0x0D, () -> {
			int res = mmu.reg_8.get('C')-1;
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.reg_8.put('C', (byte) res);
		});
		// LD HL, nn
		put((byte) 0x21, () -> {
			byte lower = mmu.ram.get(mmu.PC++);
			byte upper = mmu.ram.get(mmu.PC++);
			mmu.reg_16.put("HL", (short)((upper << 8) + lower));
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
		// LDH (n),A
		put((byte) 0xE0, () -> {
			short addr = (short) (0xFF00 + mmu.ram.get(mmu.PC++));
			mmu.ram.put(addr, mmu.reg_8.get('A'));
		});
		// LDH A,(n)
		put((byte) 0xF0, () -> {
			short addr = (short) (0xFF00 + mmu.ram.get(mmu.PC++));
			mmu.reg_8.put('A', mmu.ram.get(addr));
		});
		// DI
		put((byte) 0xF3, () -> {
			interruptable = false;
		});
		// EI
		put((byte) 0xFB, () -> {
			interruptable = true;
		});
		// CP #
		put((byte) 0xFE, () -> {
			byte a = mmu.reg_8.get('A');
			byte n = mmu.ram.get(mmu.PC++);
			byte res = (byte) (a - n);
			mmu.set_flag('Z', res == 0);
			mmu.set_flag('N', true);
			mmu.set_flag('H', (res & 0xF) == 0);
			mmu.set_flag('C', a < n);
		});
		// long opcode
		put((byte) 0xCB, () -> {
			cb_opcodes.get(mmu.ram.get(mmu.PC++)).exec();
		});
	}};
	private final Map<Byte, Opcode> cb_opcodes = new HashMap<Byte, Opcode>(){{
	}};
}
