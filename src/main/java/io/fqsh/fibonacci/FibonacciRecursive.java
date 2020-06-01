package io.fqsh.fibonacci;

public class FibonacciRecursive {
    public static long calculate(long n) {
        if (n == 0 || n == 1) return n;

        return calculate(n - 1) + calculate(n - 2);
    }
}
