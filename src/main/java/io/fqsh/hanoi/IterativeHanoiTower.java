package io.fqsh.hanoi;

import java.util.ArrayList;

public class IterativeHanoiTower {
    private final int disksNumber;
    private final ArrayList<String> iterativeSteps;

    public IterativeHanoiTower(int disksNumber) {
        this.disksNumber = disksNumber;
        this.iterativeSteps = new ArrayList<>();
    }

    private int countNumberOfSteps() {
        return (int) (Math.pow(2, disksNumber) - 1);
    }

    private int getDiskToMoveNumber(int step) {
        int checkedStep = step + 1;
        int stepDiskNumber = 0;

        while (checkedStep % 2 == 0) {
            checkedStep /= 2;
            stepDiskNumber++;
        }

        return stepDiskNumber;
    }

    private int getSourcePeg(int step, int diskNumber) {
        return (countDiskMoves(step, diskNumber) * checkMoveDestination(diskNumber)) % 3;
    }

    private int getTargetPeg(int sourcePeg, int diskNumber) {
        return (sourcePeg + checkMoveDestination(diskNumber)) % 3;
    }

    private int countDiskMoves(int step, int diskNumber) {
        while (diskNumber != 0) {
            step /= 2;
            diskNumber--;
        }

        return (step + 1) / 2;
    }

    private int checkMoveDestination(int diskNumber) {
        return 2 - ((disksNumber + diskNumber) % 2);
    }

    private void saveMove(int diskNumber, int sourcePeg, int targetPeg) {
        this.iterativeSteps.add("Disk " + diskNumber + " from " + sourcePeg + " to " + targetPeg + "");
    }

    public int getNumberOfMoves() {
        return this.iterativeSteps.size();
    }

    public void solveIterativeHanoiTower() {
        for (int step = 0; step < countNumberOfSteps(); step++) {
            int stepDiskNumber = getDiskToMoveNumber(step);

            int sourcePeg = getSourcePeg(step, stepDiskNumber);
            int targetPeg = getTargetPeg(sourcePeg, stepDiskNumber);

            saveMove(stepDiskNumber, sourcePeg, targetPeg);
        }
    }
}
