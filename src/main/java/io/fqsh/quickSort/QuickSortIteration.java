package io.fqsh.quickSort;

public class QuickSortIteration {
    public static void calculate(int[] sortedArray, int leftIndex, int rightIndex) {
        int[] stack = new int[rightIndex - leftIndex + 1];
        int top = 0;

        stack[top] = leftIndex;
        stack[++top] = rightIndex;

        while (top >= 0) {
            rightIndex = stack[top--];
            leftIndex = stack[top--];

            int separator = separate(sortedArray, leftIndex, rightIndex);

            if ((separator - 1) > leftIndex) {
                stack[++top] = leftIndex;
                stack[++top] = rightIndex - 1;
            }

            if ((separator + 1) < rightIndex) {
                stack[++top] = separator + 1;
                stack[++top] = rightIndex;
            }
        }
    }

    private static int separate(int[] separatedArray, int leftIndex, int rightIndex) {
        int separatorValueRight = separatedArray[rightIndex];
        int separatorLeftIndex = leftIndex - 1;

        for (int s = leftIndex; s < rightIndex; s++) {
            if (separatedArray[s] < separatorValueRight) {
                separatorLeftIndex++;

                int tmp = separatedArray[separatorLeftIndex];
                separatedArray[separatorLeftIndex] = separatedArray[s];
                separatedArray[s] = tmp;
            }
        }

        int tmp = separatedArray[separatorLeftIndex + 1];
        separatedArray[separatorLeftIndex + 1] = separatedArray[rightIndex];
        separatedArray[rightIndex] = tmp;

        return separatorLeftIndex + 1;
    }
}
