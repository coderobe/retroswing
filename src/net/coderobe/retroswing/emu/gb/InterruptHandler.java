package net.coderobe.retroswing.emu.gb;

public class InterruptHandler {
	private final Memory mmu;
	public boolean enabled = true;

	public InterruptHandler(Memory memory) {
		this.mmu = memory;
	}

	public void handle() {
		if (!enabled) {
			return;
		}
		enabled = false;

		byte interruptFlag = mmu.ram.get(mmu.reg_io_loc.get("IF"));
		byte interruptEnable = mmu.ram.get(mmu.reg_io_loc.get("IE"));

		if (interruptFlag > 0) {
			// V-Blank Interrupt
			if ((interruptFlag & 0x01) != 0 && (interruptEnable & 0x01) != 0) {
				executeInterrupt((short) 0x0040);
				mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (interruptFlag & ~0x01));
			}

			// LCD STAT Interrupt
			if ((interruptFlag & 0x02) != 0 && (interruptEnable & 0x02) != 0) {
				executeInterrupt((short) 0x0048);
				mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (interruptFlag & ~0x02));
			}

			// Timer Interrupt
			if ((interruptFlag & 0x04) != 0 && (interruptEnable & 0x04) != 0) {
				executeInterrupt((short) 0x0050);
				mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (interruptFlag & ~0x04));
			}

			// Serial Interrupt
			if ((interruptFlag & 0x08) != 0 && (interruptEnable & 0x08) != 0) {
				executeInterrupt((short) 0x0058);
				mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (interruptFlag & ~0x08));
			}

			// Joypad Interrupt
			if ((interruptFlag & 0x10) != 0 && (interruptEnable & 0x10) != 0) {
				executeInterrupt((short) 0x0060);
				mmu.ram.put(mmu.reg_io_loc.get("IF"), (byte) (interruptFlag & ~0x10));
			}
		}
	}

	private void executeInterrupt(short address) {
		// Push current PC to stack
		byte lower = (byte)mmu.PC;
		byte upper = (byte)(mmu.PC>>>8);
		mmu.ram.put(mmu.SP--, upper);
		mmu.ram.put(mmu.SP--, lower);

		// Jump to interrupt address
		mmu.PC = address;
	}

}
