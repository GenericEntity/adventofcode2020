public class MaskInstruction extends Instruction {
    public final String bitmask;

    public MaskInstruction(String type, String bitmask) {
        super(type);
        this.bitmask = bitmask;
    }
}
