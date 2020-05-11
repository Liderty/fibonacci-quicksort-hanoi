package io.fqsh.hanoi;

import java.util.ArrayList;

public class RecursiveHanoiTower {

    ArrayList<String> recursiveSteps;
    int disksNumber;

    RecursiveHanoiTower(int disksNumber) {
        this.recursiveSteps = new ArrayList<String>();
        this.disksNumber = disksNumber;
    }

    private void recursvieHanoiTower(int disks_number, char source_peg, char target_peg, char auxiliary_peg) {
        if (disks_number > 0) {
            recursvieHanoiTower(disks_number - 1, source_peg, auxiliary_peg, target_peg);
            this.recursiveSteps.add("Disk " + disks_number + " from " + source_peg + " to " + target_peg + "");
            recursvieHanoiTower(disks_number - 1, auxiliary_peg, target_peg, source_peg);
        }
    }

    public void solveRecursvieHanoiTower() {
        recursvieHanoiTower(disksNumber, 'A', 'B', 'C');
    }

    public int getNumberOfMoves() {
        return this.recursiveSteps.size();
    }

}
