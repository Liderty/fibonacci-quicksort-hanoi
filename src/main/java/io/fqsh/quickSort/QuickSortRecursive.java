package io.fqsh.quickSort;

public class QuickSortRecursive {
    public static void calculate(int[] sortedArray, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int separationIndex = separate(sortedArray, leftIndex, rightIndex);

            calculate(sortedArray, leftIndex, separationIndex - 1);
            calculate(sortedArray, separationIndex + 1, rightIndex);
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
