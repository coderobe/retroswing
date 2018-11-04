package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	public Memory mmu = new Memory();
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
		// long opcode
		put((byte) 0xCB, () -> {
			cb_opcodes.get(mmu.ram.get(mmu.PC++)).exec();
		});
	}};
	private final Map<Byte, Opcode> cb_opcodes = new HashMap<Byte, Opcode>(){{
	}};
}
