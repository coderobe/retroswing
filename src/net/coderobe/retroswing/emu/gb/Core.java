package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	private final Map<Byte, Opcode> opcodes = new HashMap<Byte, Opcode>(){{
		// NOP
		put((byte) 0x00, () -> {
			// hurr
		});
		// long opcode
		put((byte) 0xCB, () -> {
			cb_opcodes.get(mmu.ram.get(mmu.PC++)).exec();
		});
	}};
	private final Map<Byte, Opcode> cb_opcodes = new HashMap<Byte, Opcode>(){{
	}};
}
