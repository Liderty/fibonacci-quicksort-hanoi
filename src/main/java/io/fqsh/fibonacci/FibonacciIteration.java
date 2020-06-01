package io.fqsh.fibonacci;

public class FibonacciIteration {
    public static long calculate(long n) {
        if (n == 0 || n == 1) return n;

        long a = 1;
        long b = 1;
        long temporary;

        for (int i = 0; i < (n - 2); i++) {
            temporary = a;
            a = b;
            b = temporary;
            b += a;
        }

        return b;
    }
}
