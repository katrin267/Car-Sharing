import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int lower = scanner.nextInt();
        int upper = scanner.nextInt();

        Random random = new Random(lower + upper);
        int intervalLength = upper - lower + 1;
        int sum = 0;

        for (int i = 0; i < n; i++) {
            sum += random.nextInt(intervalLength) + lower;
        }

        System.out.println(sum);
    }
}