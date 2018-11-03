package net.coderobe.retroswing.emu.gb;

import java.util.HashMap;
import java.util.Map;

public class Core {
	private final Map<Integer, Opcode> opcodes = new HashMap<Integer, Opcode>(){{
		// NOP
		put(0x00, new Opcode() {
			@Override
			public void exec() {
				// hurr
			}
		});
	}};
}
