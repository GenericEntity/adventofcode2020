import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Data {
    public final ArrayList<Instruction> instructions;

    public Data(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }
}

class BitmaskPair {
    public final long onesMask;
    public final long zeroesMask;

    public BitmaskPair(long onesMask, long zeroesMask) {
        this.onesMask = onesMask;
        this.zeroesMask = zeroesMask;
    }
}

public class Day14 {
    private static final String MEM_TYPE = "mem";
    private static final String MASK_TYPE = "mask";

    private static Data readData() {
        Scanner sc = new Scanner(System.in);
        Pattern typePattern = Pattern.compile("(" + MEM_TYPE + "|" + MASK_TYPE + ")");
        Pattern maskPattern = Pattern.compile("mask = (\\w+)");
        Pattern memPattern = Pattern.compile("mem\\[(\\d+)\\] = (\\d+)");
        
        Data data = new Data(new ArrayList<>());
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher typeMatcher = typePattern.matcher(line);
            if (typeMatcher.find()) {
                String type = typeMatcher.group(1);
                if (type.equals(MEM_TYPE)) {
                    Matcher memMatcher = memPattern.matcher(line);
                    if (memMatcher.find()) {
                        long addr = Long.parseLong(memMatcher.group(1));
                        long val = Long.parseLong(memMatcher.group(2));
                        data.instructions.add(new MemInstruction(type, addr, val));
                    }
                } else if (type.equals(MASK_TYPE)) {
                    Matcher maskMatcher = maskPattern.matcher(line);
                    if (maskMatcher.find()) {
                        String bitmask = maskMatcher.group(1);
                        data.instructions.add(new MaskInstruction(type, bitmask));
                    }
                } else {
                    System.err.println(String.format("Unknown type: '%s'", type));
                }
            }
        }

        sc.close();

        return data;
    }

    private static void task1(Data data) {
        // Maps addresses to values (we want to avoid having 2^36 addresses
        // since most will contain the value 0), so only addresses written to are stored
        HashMap<Long, Long> memory = new HashMap<>();
        // onesMask stores all the bits that need to be forced to 1
        long onesMask = 0L;
        // zeroesMask stores all bits that need to be forced to 0
        long zeroesMask = ~(0L);

        for (Instruction instr : data.instructions) {
            if (instr.type.equals(MEM_TYPE)) {
                MemInstruction memInstr = (MemInstruction)instr;
                // Apply both masks to value to force relevant bits to 1 or 0
                // then write that value to 'memory'
                memory.put(memInstr.address, (memInstr.value | onesMask) & zeroesMask);
            } else if (instr.type.equals(MASK_TYPE)) {
                MaskInstruction maskInstr = (MaskInstruction)instr;
                // Construct new bitmasks
                onesMask = 0L;
                zeroesMask = ~(0L);
                for (int i = 0; i < maskInstr.bitmask.length(); i++) {
                    int offset = maskInstr.bitmask.length() - i - 1;
                    char bit = maskInstr.bitmask.charAt(i);
                    if (bit == '1') {
                        onesMask |= (1L << offset);
                    } else if (bit == '0') {
                        zeroesMask &= ~(1L << offset);
                    }
                }
            } else {
                System.err.println(String.format("Unknown type: '%s'", instr.type));
            }
        }

        // Sum all non-zero values in memory
        BigInteger sum = BigInteger.ZERO;
        for (long val : memory.values()) {
            sum = sum.add(BigInteger.valueOf(val));
        }
        System.out.println(String.format("Task 1: %d", sum));
    }

    private static void task2(Data data) {
        HashMap<Long, Long> memory = new HashMap<>();
        ArrayList<BitmaskPair> bitMasks = new ArrayList<>();

        for (Instruction instr : data.instructions) {
            if (instr.type.equals(MEM_TYPE)) {
                MemInstruction memInstr = (MemInstruction)instr;
                // Write value to every address specified by the current bitmasks
                for (BitmaskPair pair : bitMasks) {
                    memory.put((memInstr.address | pair.onesMask) & pair.zeroesMask, memInstr.value);
                }
            } else if (instr.type.equals(MASK_TYPE)) {
                MaskInstruction maskInstr = (MaskInstruction)instr;
                bitMasks.clear();

                // get the bits that will be forced to 1
                // and save the positions of all floating bits
                long onesMaskBase = 0L;
                ArrayList<Integer> floatingPos = new ArrayList<>();
                for (int i = 0; i < maskInstr.bitmask.length(); i++) {
                    int offset = maskInstr.bitmask.length() - i - 1;
                    char bit = maskInstr.bitmask.charAt(i);
                    if (bit == 'X') {
                        floatingPos.add(offset);
                    } else if (bit == '1') {
                        onesMaskBase |= (1L << offset);
                    }
                }

                /**
                 * Generate the mask permutations
                 * 
                 * Let n be the number of X's in the bitmask
                 * There are 2^n permutations, which can be represented as the
                 * bits of the long values from 0 through (2^n)-1. We take each
                 * value and assign the bits to the relevant bitmask (zeroesMask
                 * or onesMask at the offset where there was an X). In this way,
                 * we build every permutation of bitmask needed
                 */
                long nPerms = 1L << floatingPos.size();
                for (long perm = 0; perm < nPerms; perm++) {
                    long onesMask = onesMaskBase;
                    long zeroesMask = ~(0L);
                    for (int i = 0; i < floatingPos.size(); i++) {
                        int offset = floatingPos.get(i);
                        long ithBit = perm & (1L << i);
                        if (ithBit == 0) {
                            zeroesMask &= ~(1L << offset);
                        } else {
                            // we did not move the ith bit to the ones place so we
                            // only distinguish 0 and non-zero which is all we need
                            // to know if there was a 1 there or a 0.
                            onesMask |= 1L << offset;
                        }
                    }
                    bitMasks.add(new BitmaskPair(onesMask, zeroesMask));
                }
            } else {
                System.err.println(String.format("Unknown type: '%s'", instr.type));
            }
        }

        // Sum all non-zero values in memory
        BigInteger sum = BigInteger.ZERO;
        for (long val : memory.values()) {
            sum = sum.add(BigInteger.valueOf(val));
        }
        System.out.println(String.format("Task 2: %d", sum));
    }

    public static void main(String[] args) {
        Data data = readData();
        task1(data);
        task2(data);
    }
}
