package pt.cubesolvers.solver;

import cs.cube555.Search;
import pt.cubesolvers.model.Cube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Solver5x5 {
    private static final char[] FACE_ORDER = {'U', 'R', 'F', 'D', 'L', 'B'};

    private final Cube cube;

    public Solver5x5(Cube cube) {
        if (cube.size() != 5) {
            throw new IllegalArgumentException("This solver supports only 5x5 cubes.");
        }
        this.cube = cube.copy();
    }

    public Optional<List<String>> solve() {
        if (cube.isSolved()) {
            return Optional.of(List.of());
        }

        String facelets = facelets();
        Search.init();
        String[] reduction = new Search().solveReduction(facelets, 0);
        if (reduction[0].startsWith("Error ")) {
            throw new IllegalArgumentException("Invalid 5x5 cube: solver returned "
                    + reduction[0] + ".");
        }
        if (reduction[1] == null) {
            throw new IllegalStateException("The 5x5 solver did not return a reduced 3x3 state.");
        }

        String finish = new cs.min2phase.Search().solution(reduction[1], 21, 10_000_000, 0, 0);
        if (finish.startsWith("Error ")) {
            throw new IllegalStateException("The reduced 3x3 cube could not be solved: " + finish + ".");
        }

        List<String> solution = new ArrayList<>();
        appendMoves(solution, reduction[0], true);
        appendMoves(solution, finish, false);
        return Optional.of(List.copyOf(solution));
    }

    private String facelets() {
        Map<Integer, Character> faceByColor = new HashMap<>();
        for (char face : FACE_ORDER) {
            int centerColor = cube.colorAt(face, 2, 2);
            if (faceByColor.put(centerColor, face) != null) {
                throw new IllegalArgumentException("Each face center must have a unique color.");
            }
        }

        StringBuilder facelets = new StringBuilder(150);
        int[] counts = new int[6];
        for (char face : FACE_ORDER) {
            for (int row = 0; row < 5; row++) {
                for (int column = 0; column < 5; column++) {
                    int color = cube.colorAt(face, row, column);
                    if (color < 0 || color >= counts.length) {
                        throw new IllegalArgumentException("The cube contains an unknown color.");
                    }
                    Character mappedFace = faceByColor.get(color);
                    if (mappedFace == null) {
                        throw new IllegalArgumentException("The cube contains a color without a center.");
                    }
                    counts[color]++;
                    facelets.append(mappedFace);
                }
            }
        }
        for (int count : counts) {
            if (count != 25) {
                throw new IllegalArgumentException("A valid 5x5 cube has twenty-five stickers of each color.");
            }
        }
        return facelets.toString();
    }

    private static void appendMoves(List<String> solution, String moves, boolean wideMoves) {
        for (String move : moves.trim().split("\\s+")) {
            if (move.isEmpty()) {
                continue;
            }
            char face = move.charAt(0);
            if (wideMoves && Character.isLowerCase(face)) {
                solution.add(Character.toUpperCase(face) + "w" + move.substring(1));
            } else {
                solution.add(move);
            }
        }
    }
}
