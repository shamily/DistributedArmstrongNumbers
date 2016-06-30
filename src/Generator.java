import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Generator {
    private static int N;
    private static int[] digitsMultiSet;
    private static int[] testMultiSet;

    private static List<BigInteger> results;
    private static BigInteger maxPow;
    private static BigInteger minPow;
    private static BigInteger[][] pows;

    private static Channel channel = null;
    private static String queueName = null;

    private static void genPows(int N) {
        pows = new BigInteger[10][N + 1];
        for (int i = 0; i < pows.length; i++) {
            BigInteger p = BigInteger.ONE;
            for (int j = 0; j < pows[i].length; j++) {
                pows[i][j] = p;
                p = p.multiply(BigInteger.valueOf(i));
            }
        }
    }

    private static void prepareLimits(int N) {
        minPow = BigInteger.ONE;
        for (int i = 0; i < N - 1; i++) {
            minPow = minPow.multiply(BigInteger.TEN);
        }
        maxPow = minPow.multiply(BigInteger.TEN);
    }

    private static boolean check(BigInteger pow) {
        int compareMax = pow.compareTo(maxPow);
        if (compareMax >= 0) return false;
        int compareMin = pow.compareTo(minPow);
        if (compareMin < 0) return false;

        for (int i = 0; i < 10; i++) {
            testMultiSet[i] = 0;
        }

        while (pow.compareTo(BigInteger.ZERO) != 0) {
            BigInteger[] resultAndRemainder = pow.divideAndRemainder(BigInteger.TEN);
            int i = resultAndRemainder[1].intValue();
            testMultiSet[i]++;
            if (testMultiSet[i] > digitsMultiSet[i]) return false;
            pow = resultAndRemainder[0];
        }

        for (int i = 0; i < 10; i++) {
            if (testMultiSet[i] != digitsMultiSet[i]) return false;
        }

        return true;
    }

    private static void publishNumber(BigInteger i) throws IOException {
        channel.basicPublish("", queueName, null, i.toString().getBytes());
        System.out.println(" [x] Found and Sent '" + i.toString() + "'");
    }

    private static void search(int digit, int unused, BigInteger pow) throws IOException {
        int compareMax = pow.compareTo(maxPow);
        if (compareMax >= 0) return;

        if (digit == -1) {
            if (check(pow)) publishNumber(pow);
            return;
        }

        if (digit == 0) {
            digitsMultiSet[digit] = unused;
            search(digit - 1, 0, pow.add(pows[digit][N].multiply(BigInteger.valueOf(unused))));
        } else {
            // Check if we can generate more than minimum
            if ((pow.add(pows[digit][N].multiply(BigInteger.valueOf(unused)))).compareTo(minPow) < 0) return;

            BigInteger bi = pow;
            for (int i = 0; i <= unused; i++) {
                digitsMultiSet[digit] = i;
                search(digit - 1, unused - i, bi);
                if (i != unused) bi = bi.add(pows[digit][N]);
            }
        }
    }

    public static void generate(int N, Channel channel, String queueName) throws IOException {
        Generator.channel = channel;
        Generator.queueName = queueName;
        Generator.N = N;

        genPows(N);
        digitsMultiSet = new int[10];
        testMultiSet = new int[10];
        prepareLimits(N);

        search(9, N, BigInteger.ZERO);
    }
}
