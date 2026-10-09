package pt.cubesolvers.solver;

import org.junit.jupiter.api.Test;
import pt.cubesolvers.model.Cube;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Solver3x3Test {
    @Test
    void solvesMeaningfulScrambleWithoutMutatingInput() {
        Cube cube = new Cube(3);
        cube.applySequence("R U R' F2 D L2 U' B R2 F' U2 L D' B2 R U F2 L' D2 B'");
        String scrambled = cube.serialize();

        var solution = new Solver3x3(cube).solve().orElseThrow();
        Cube solved = cube.copy();
        solved.applySequence(String.join(" ", solution));

        assertTrue(solved.isSolved());
        assertEquals(scrambled, cube.serialize());
        assertTrue(solution.size() <= 20);
    }

    @Test
    void returnsEmptyWhenDepthLimitCannotSolveScramble() {
        Cube cube = new Cube(3);
        cube.applySequence("R U");

        assertTrue(new Solver3x3(cube, 1, 10).solve().isEmpty());
    }

    @Test
    void findsTheShortestSolutionForAShortScramble() {
        Cube cube = new Cube(3);
        cube.applySequence("R U F");

        var solution = new Solver3x3(cube, 6, 10).solve().orElseThrow();

        assertEquals(3, solution.size());
        cube.applySequence(String.join(" ", solution));
        assertTrue(cube.isSolved());
    }

    @Test
    void rejectsCubesOfOtherSizes() {
        assertThrows(IllegalArgumentException.class, () -> new Solver3x3(new Cube(2)));
    }

    @Test
    void rejectsDepthLimitsAboveGodNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Solver3x3(new Cube(3), 21, 10));
    }

    @Test
    void rejectsAnUnsolvableSingleEdgeFlip() throws ReflectiveOperationException {
        Cube cube = new Cube(3);
        Field facesField = Cube.class.getDeclaredField("faces");
        facesField.setAccessible(true);
        int[][][] faces = (int[][][]) facesField.get(cube);
        int temporary = faces[0][1][2];
        faces[0][1][2] = faces[5][0][1];
        faces[5][0][1] = temporary;

        assertThrows(IllegalArgumentException.class, () -> new Solver3x3(cube).solve());
    }

    @Test
    void returnsAnEmptySolutionForSolvedCube() {
        assertEquals(java.util.List.of(), new Solver3x3(new Cube(3)).solve().orElseThrow());
    }
}
