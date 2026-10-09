package pt.cubesolvers.solver;

import cs.threephase.Search;
import pt.cubesolvers.model.Cube;

import java.util.List;
import java.util.Optional;

public final class Solver4x4 {
    private static final char[] FACE_BY_COLOR = {'U', 'D', 'F', 'B', 'L', 'R'};
    private static final char[] FACE_ORDER = {'U', 'R', 'F', 'D', 'L', 'B'};

    private final Cube cube;

    public Solver4x4(Cube cube) {
        if (cube.size() != 4) {
            throw new IllegalArgumentException("This solver supports only 4x4 cubes.");
        }
        this.cube = cube.copy();
    }

    public Optional<List<String>> solve() {
        if (cube.isSolved()) {
            return Optional.of(List.of());
        }

        Search search = new Search();
        search.with_rotation = false;
        String result = search.solution(facelets());
        if (result.isBlank()) {
            throw new IllegalStateException("The 4x4 solver returned an empty solution for a scrambled cube.");
        }
        return Optional.of(List.of(result.trim().split("\\s+")));
    }

    private String facelets() {
        StringBuilder result = new StringBuilder(96);
        int[] counts = new int[FACE_BY_COLOR.length];
        for (char face : FACE_ORDER) {
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 4; column++) {
                    int color = cube.colorAt(face, row, column);
                    if (color < 0 || color >= FACE_BY_COLOR.length) {
                        throw new IllegalArgumentException("The cube contains an unknown color.");
                    }
                    counts[color]++;
                    result.append(FACE_BY_COLOR[color]);
                }
            }
        }
        for (int count : counts) {
            if (count != 16) {
                throw new IllegalArgumentException("A valid 4x4 cube has sixteen stickers of each color.");
            }
        }
        return result.toString();
    }
}
