package pt.cubesolvers.solver;

import org.junit.jupiter.api.Test;
import pt.cubesolvers.model.Cube;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Solver2x2Test {
    @Test
    void solutionRestoresScrambledCubeWithoutMutatingInput() {
        Cube cube = new Cube(2);
        cube.applySequence("R U R' U'");
        String scrambled = cube.serialize();

        var solution = new Solver2x2(cube).solve().orElseThrow();
        Cube solved = cube.copy();
        solved.applySequence(String.join(" ", solution));

        assertTrue(solved.isSolved());
        assertTrue(cube.serialize().equals(scrambled));
    }

    @Test
    void solutionHandlesAFullScrambleAcrossCubeOrientations() {
        Cube cube = new Cube(2);
        cube.applySequence("F R U' R' U' R U R' F' R U R' U' R' F R F");

        var solution = new Solver2x2(cube).solve().orElseThrow();
        cube.applySequence(String.join(" ", solution));

        assertTrue(cube.isSolved());
    }
}
