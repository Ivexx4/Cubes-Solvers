package pt.cubesolvers.solver;

import org.junit.jupiter.api.Test;
import pt.cubesolvers.model.Cube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Solver5x5Test {
    @Test
    void solvesWideTurnScrambleWithoutMutatingInput() {
        Cube cube = new Cube(5);
        cube.applySequence("Rw U F2 2Uw' R2");
        String scrambled = cube.serialize();

        var solution = new Solver5x5(cube).solve().orElseThrow();
        Cube solved = cube.copy();
        solved.applySequence(String.join(" ", solution));

        assertTrue(solved.isSolved());
        assertEquals(scrambled, cube.serialize());
    }

    @Test
    void returnsEmptySolutionForSolvedCube() {
        assertEquals(java.util.List.of(), new Solver5x5(new Cube(5)).solve().orElseThrow());
    }

    @Test
    void rejectsCubesOfOtherSizes() {
        assertThrows(IllegalArgumentException.class, () -> new Solver5x5(new Cube(4)));
    }
}
