package testMethod;

public class test {
    public static void main(String[] args) {
        String str = "892b8a7f-30a8-4bcd-8be6-6742e9a96dcc-4eaa20d5-18a7-408a-a3a0-b6b541c7f795";
        String[] parts = str.split("-");
        String part1 = parts[0] + "-" + parts[1] + "-" + parts[2] + "-" + parts[3] + "-" + parts[4];
        String part2 = parts[5] + "-" + parts[6] + "-" + parts[7] + "-" + parts[8] + "-" + parts[9];
        System.out.println(part1);
        System.out.println(part2);
    }
}
