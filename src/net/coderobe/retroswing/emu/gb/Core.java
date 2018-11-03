package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	private final Map<Byte, Opcode> opcodes = new HashMap<>(){{
		// NOP
		put((byte) 0x00, () -> {
			// hurr
		});
	}};
}
