public class MemInstruction extends Instruction {
    public final long address;
    public final long value;

    public MemInstruction(String type, long addr, long val) {
        super(type);
        this.address = addr;
        this.value = val;
    }
}
