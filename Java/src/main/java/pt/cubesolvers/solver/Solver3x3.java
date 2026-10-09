package pt.cubesolvers.solver;

import pt.cubesolvers.model.Cube;
import cs.min2phase.Search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Solver3x3 {
    private static final int GOD_NUMBER_HTM = 20;
    private static final List<Character> KOCIEMBA_FACE_ORDER =
            List.of('U', 'R', 'F', 'D', 'L', 'B');
    private static final long PROBES_PER_SECOND = 10_000_000L;
    private final Cube cube;
    private final int maxDepth;
    private final long probeLimit;

    public Solver3x3(Cube cube) {
        this(cube, GOD_NUMBER_HTM, 10);
    }

    /**
     * The upstream solver caps phase-two probes rather than elapsed time; timeoutSeconds is
     * retained for compatibility and converted to a probe budget.
     */
    public Solver3x3(Cube cube, int maxDepth, long timeoutSeconds) {
        if (cube.size() != 3) {
            throw new IllegalArgumentException("This solver supports only 3x3 cubes.");
        }
        if (maxDepth < 1 || maxDepth > GOD_NUMBER_HTM || timeoutSeconds < 1) {
            throw new IllegalArgumentException(
                    "Depth must be between 1 and " + GOD_NUMBER_HTM
                            + " and timeout must be positive.");
        }
        this.cube = cube.copy();
        this.maxDepth = maxDepth;
        this.probeLimit = timeoutSeconds > Long.MAX_VALUE / PROBES_PER_SECOND
                ? Long.MAX_VALUE
                : timeoutSeconds * PROBES_PER_SECOND;
    }

    public Optional<List<String>> solve() {
        if (cube.isSolved()) {
            return Optional.of(List.of());
        }
        String result = new Search().solution(facelets(), maxDepth, probeLimit, 0, 0);
        if (result.startsWith("Error ")) {
            if (result.equals("Error 7") || result.equals("Error 8")) {
                return Optional.empty();
            }
            throw new IllegalArgumentException("Invalid 3x3 cube: min2phase returned " + result + ".");
        }
        return Optional.of(List.of(result.trim().split("\\s+")));
    }

    private String facelets() {
        Map<Integer, Character> colorToFace = new HashMap<>();
        for (char face : KOCIEMBA_FACE_ORDER) {
            int centerColor = cube.colorAt(face, 1, 1);
            if (colorToFace.put(centerColor, face) != null) {
                throw new IllegalArgumentException("Each face center must have a unique color.");
            }
        }
        if (colorToFace.size() != 6) {
            throw new IllegalArgumentException("Each face center must have a unique color.");
        }

        StringBuilder result = new StringBuilder(54);
        int[] counts = new int[6];
        for (char face : KOCIEMBA_FACE_ORDER) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    int color = cube.colorAt(face, row, column);
                    Character mappedFace = colorToFace.get(color);
                    if (mappedFace == null) {
                        throw new IllegalArgumentException("The cube contains a color without a center.");
                    }
                    counts[KOCIEMBA_FACE_ORDER.indexOf(mappedFace)]++;
                    result.append(mappedFace);
                }
            }
        }
        for (int count : counts) {
            if (count != 9) {
                throw new IllegalArgumentException("A valid 3x3 cube has nine stickers of each color.");
            }
        }
        return result.toString();
    }
}
