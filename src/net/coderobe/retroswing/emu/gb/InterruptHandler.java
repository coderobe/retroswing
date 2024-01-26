package net.coderobe.retroswing.emu.gb;

public class InterruptHandler {
	private final Memory mmu;
	private static final short INTERRUPT_FLAG = (short) 0xFF0F;
	private static final short INTERRUPT_ENABLE = (short) 0xFFFF;

	public InterruptHandler(Memory memory) {
		this.mmu = memory;
	}

	public void handle() {
		byte interruptFlag = mmu.ram.get(INTERRUPT_FLAG);
		byte interruptEnable = mmu.ram.get(INTERRUPT_ENABLE);

		if (interruptFlag > 0) {
			// V-Blank Interrupt
			if ((interruptFlag & 0x01) != 0 && (interruptEnable & 0x01) != 0) {
				executeInterrupt(0x0040);
				mmu.ram.put(INTERRUPT_FLAG, (byte) (interruptFlag & ~0x01));
			}

			// LCD STAT Interrupt
			if ((interruptFlag & 0x02) != 0 && (interruptEnable & 0x02) != 0) {
				executeInterrupt(0x0048);
				mmu.ram.put(INTERRUPT_FLAG, (byte) (interruptFlag & ~0x02));
			}

			// Timer Interrupt
			if ((interruptFlag & 0x04) != 0 && (interruptEnable & 0x04) != 0) {
				executeInterrupt(0x0050);
				mmu.ram.put(INTERRUPT_FLAG, (byte) (interruptFlag & ~0x04));
			}

			// Serial Interrupt
			if ((interruptFlag & 0x08) != 0 && (interruptEnable & 0x08) != 0) {
				executeInterrupt(0x0058);
				mmu.ram.put(INTERRUPT_FLAG, (byte) (interruptFlag & ~0x08));
			}

			// Joypad Interrupt
			if ((interruptFlag & 0x10) != 0 && (interruptEnable & 0x10) != 0) {
				executeInterrupt(0x0060);
				mmu.ram.put(INTERRUPT_FLAG, (byte) (interruptFlag & ~0x10));
			}
		}
	}

	private void executeInterrupt(int address) {
		// Push current PC to stack
		byte lower = (byte)mmu.PC;
		byte upper = (byte)(mmu.PC>>>8);
		mmu.ram.put(mmu.SP--, upper);
		mmu.ram.put(mmu.SP--, lower);

		// Jump to interrupt address
		mmu.PC = (short) (address & 0xFFFF);
	}

}
