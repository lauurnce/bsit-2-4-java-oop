package mar_7_2026;

public class SeatworkOperatorExecutionTrace {
	public static void main(String[] args) {
        int b = 56;
        int d = 54;
        int f = 34;
        int h = 40;

        System.out.println((b++ < ++f) & (++d > --h));   // 1
        System.out.println(++b & d++);                   // 2
        System.out.println((b++ * ++d));                 // 3
        System.out.println((--f < ++h) && (++d < --b));  // 4
        System.out.println((++b + ++f));                 // 5
        System.out.println((h++ > ++f) && (++b == --h)); // 6
        System.out.println((b++ | ++f));                 // 7
        System.out.println(++h - d++);                   // 8
        System.out.println((--b * ++f));                 // 9
        System.out.println((--f < ++h) & (++d < --b));   // 10
        System.out.println((b++ > ++f) & (++f > --f));   // 11
        System.out.println(b++ & ++d);                   // 12
        System.out.println(++b - d++);                   // 13
        System.out.println((b++ * ++f));                 // 14
    }

}
