package io.fqsh.hanoi;

import java.util.ArrayList;

public class IterativeHanoiTower {

    private int disksNumber;
    private ArrayList<String> iterativeSteps;

    IterativeHanoiTower(int disks_number) {
        this.disksNumber = disks_number;
        this.iterativeSteps = new ArrayList<String>();
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

    private int getSoutcePeg(int step, int disk_number) {
        return (countDiskMoves(step, disk_number) * checkMoveDestination(disk_number)) % 3;
    }

    private int getTargetPeg(int source_peg, int disk_number) {
        return (source_peg + checkMoveDestination(disk_number)) % 3;
    }

    private int countDiskMoves(int step, int disk_number) {
        while (disk_number != 0) {
            step /= 2;
            disk_number--;
        }

        return (step + 1) / 2;
    }

    private int checkMoveDestination(int disk_number) {
        return 2 - ((disksNumber + disk_number) % 2);
    }

    private void saveMove(int disk_number, int source_peg, int target_peg) {
        this.iterativeSteps.add("Disk " + disk_number + " from " + source_peg + " to " + target_peg + "");
    }

    public int getNumberOfMoves() {
        return this.iterativeSteps.size();
    }

    public void solveIterativeHanoiTower() {
        for (int step = 0; step < countNumberOfSteps(); step++) {
            int stepDiskNumber = getDiskToMoveNumber(step);

            int source_peg = getSoutcePeg(step, stepDiskNumber);
            int target_peg = getTargetPeg(source_peg, stepDiskNumber);

            saveMove(stepDiskNumber, source_peg, target_peg);
        }
    }
}
