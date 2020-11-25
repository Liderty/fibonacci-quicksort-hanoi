package io.fqsh.hanoi;

import java.util.ArrayList;

public class RecursiveHanoiTower {
    private final ArrayList<String> recursiveSteps;
    private final int disksNumber;

    public RecursiveHanoiTower(int disksNumber) {
        this.recursiveSteps = new ArrayList<>();
        this.disksNumber = disksNumber;
    }

    private void recursiveHanoiTower(int disksNumber, char sourcePeg, char targetPeg, char auxiliaryPeg) {
        if (disksNumber > 0) {
            recursiveHanoiTower(disksNumber - 1, sourcePeg, auxiliaryPeg, targetPeg);
            this.recursiveSteps.add("Disk " + disksNumber + " from " + sourcePeg + " to " + targetPeg + "");
            recursiveHanoiTower(disksNumber - 1, auxiliaryPeg, targetPeg, sourcePeg);
        }
    }

    public void solveRecursiveHanoiTower() {
        recursiveHanoiTower(disksNumber, 'A', 'B', 'C');
    }

    public int getNumberOfMoves() {
        return this.recursiveSteps.size();
    }
}
