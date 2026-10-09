package pt.cubesolvers.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CubeTest {
    @Test
    void fourQuarterTurnsRestoreTheCubeForEveryFace() {
        for (char face : new char[]{'U', 'D', 'F', 'B', 'L', 'R'}) {
            Cube cube = new Cube(3);
            for (int turn = 0; turn < 4; turn++) {
                cube.applyMove(String.valueOf(face));
            }
            assertTrue(cube.isSolved(), "Face " + face);
        }
    }

    @Test
    void everyFaceTurnAndItsInverseRestoreAcrossCubeSizes() {
        for (int size : new int[]{2, 3, 5}) {
            for (char face : new char[]{'U', 'D', 'F', 'B', 'L', 'R'}) {
                Cube cube = new Cube(size);
                cube.applyMove(String.valueOf(face));
                cube.applyMove(face + "'");
                assertTrue(cube.isSolved(), size + "x" + size + " face " + face);
            }
        }
    }

    @Test
    void wideTurnAndItsInverseRestoreTheCube() {
        Cube cube = new Cube(5);
        cube.applyMove("3Fw");
        cube.applyMove("3Fw'");
        assertTrue(cube.isSolved());
    }

    @Test
    void invalidMovesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Cube(3).applyMove("X"));
        assertThrows(IllegalArgumentException.class, () -> new Cube(3).applyMove("4Rw"));
    }

    @Test
    void sequenceChangesTheStateAndCopyIsIndependent() {
        Cube cube = new Cube(3);
        cube.applySequence("R U R' U'");
        Cube copy = cube.copy();
        assertFalse(cube.isSolved());
        assertEquals(cube.serialize(), copy.serialize());
        copy.applyMove("R");
        assertFalse(cube.serialize().equals(copy.serialize()));
    }
}
