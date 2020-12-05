import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 {
    private static List<String> getInput() {
        ArrayList<String> passports = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        StringBuilder passport = new StringBuilder();
        passport.setLength(0);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.isEmpty()) {
                passport.append(" ");
                passport.append(line);
            } else {
                passports.add(passport.toString());
                passport.setLength(0);
            }
        }
        
        // In case the file does not end with a blank line
        if (passport.length() > 0) {
            passports.add(passport.toString());
        }

        sc.close();
        return passports;
    }

    private static void task1(List<String> passports) {
        List<String> reqFields = new ArrayList<>();
        reqFields.add("byr");
        reqFields.add("iyr");
        reqFields.add("eyr");
        reqFields.add("hgt");
        reqFields.add("hcl");
        reqFields.add("ecl");
        reqFields.add("pid");
        
        int count = 0;

        for (String passport : passports) {
            boolean valid = true;
            for (String field : reqFields) {
                if (passport.indexOf(field) == -1) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                count++;
            }
        }

        System.out.println(String.format("Task 1 Valid 'passports': %d", count));
    }

    private static boolean inRange(int val, int lowerIncl, int upperIncl) {
        return val >= lowerIncl && val <= upperIncl;
    }

    private static void task2(List<String> passports) {
        /**
         * For those not familiar with regex, see the details here:
         * https://regex101.com/r/ki9i4S/1
         * 
         * (If the link is broken, then sorry you'll have to convert the regex
         * string below manually then paste it into a regex tester)
         */
        final Pattern p = Pattern.compile(
            "(?=.*byr:(\\d{4})\\b)"
            + "(?=.*iyr:(\\d{4})\\b)"
            + "(?=.*eyr:(\\d{4})\\b)"
            + "(?=.*hgt:(\\d+)(cm|in)\\b)"
            + "(?=.*hcl:#([\\d|a-f]{6})\\b)"
            + "(?=.*ecl:(amb|blu|brn|gry|grn|hzl|oth)\\b)"
            + "(?=.*pid:(\\d{9})\\b)"
            );

        int validCount = 0;
        
        /**
         * Regex will check the presence of required fields, their lengths and
         * composition (decimal digits, hexadecimal, etc.)
         * but it cannot do numerical validation well. So we check that manually
         * here.
         */
        for (String passport : passports) {
            Matcher m = p.matcher(passport);
            if (m.find()) {
                int byr = Integer.parseInt(m.group(1));
                int iyr = Integer.parseInt(m.group(2));
                int eyr = Integer.parseInt(m.group(3));
                int hgt = Integer.parseInt(m.group(4));
                String hgtUnit = m.group(5);

                boolean byrValid = inRange(byr, 1920, 2002);
                boolean iyrValid = inRange(iyr, 2010, 2020);
                boolean eyrValid = inRange(eyr, 2020, 2030);
                boolean hgtValid;
                if (hgtUnit.equals("cm")) {
                    hgtValid = inRange(hgt, 150, 193);
                } else if (hgtUnit.equals("in")) {
                    hgtValid = inRange(hgt, 59, 76);
                } else {
                    hgtValid = false;
                }

                if (byrValid && iyrValid && eyrValid && hgtValid) {
                    validCount++;
                }
            }
        }

        System.out.println(String.format("Task 2 Valid 'passports': %d", validCount));
    }

    public static void main(String[] args) {
        List<String> passports = getInput();
        task1(passports);
        task2(passports);
    }
}
