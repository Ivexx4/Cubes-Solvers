package pt.cubesolvers.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Cube {
    private static final List<Character> FACES = List.of('U', 'D', 'F', 'B', 'L', 'R');
    private static final Map<Character, Integer> COLORS =
            Map.of('U', 0, 'D', 1, 'F', 2, 'B', 3, 'L', 4, 'R', 5);
    private static final Map<Character, Character> OPPOSITES =
            Map.of('U', 'D', 'D', 'U', 'F', 'B', 'B', 'F', 'L', 'R', 'R', 'L');
    private static final Pattern MOVE =
            Pattern.compile("^(\\d*)([UDFBLR])(w?)(2|')?$");

    private final int size;
    private final int[][][] faces;

    public Cube() {
        this(3);
    }

    public Cube(int size) {
        if (size < 2) {
            throw new IllegalArgumentException("Cube size must be at least 2.");
        }
        this.size = size;
        this.faces = new int[FACES.size()][size][size];
        for (int face = 0; face < FACES.size(); face++) {
            for (int[] row : faces[face]) {
                Arrays.fill(row, face);
            }
        }
    }

    private Cube(int size, int[][][] state) {
        this.size = size;
        this.faces = state;
    }

    public int size() {
        return size;
    }

    public int colorAt(char face, int row, int column) {
        return faces[faceIndex(face)][row][column];
    }

    public Cube copy() {
        int[][][] copy = new int[faces.length][size][size];
        for (int face = 0; face < faces.length; face++) {
            for (int row = 0; row < size; row++) {
                copy[face][row] = faces[face][row].clone();
            }
        }
        return new Cube(size, copy);
    }

    public boolean isSolved() {
        for (int face = 0; face < faces.length; face++) {
            int color = faces[face][0][0];
            for (int[] row : faces[face]) {
                for (int sticker : row) {
                    if (sticker != color) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void move(char face, int turns, int depth) {
        int faceIndex = faceIndex(face);
        if (turns < 1 || turns > 3) {
            throw new IllegalArgumentException("Turns must be 1, 2, or 3.");
        }
        if (depth < 1 || depth > size) {
            throw new IllegalArgumentException("Move depth must be between 1 and the cube size.");
        }

        for (int layer = 0; layer < depth; layer++) {
            rotateLayer(face, turns, layer);
        }
    }

    public void applyMove(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        Matcher matcher = MOVE.matcher(command.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid move: " + command);
        }

        int depth = matcher.group(1).isEmpty() ? 1 : Integer.parseInt(matcher.group(1));
        char face = matcher.group(2).charAt(0);
        boolean wide = !matcher.group(3).isEmpty();
        if (wide && matcher.group(1).isEmpty()) {
            depth = 2;
        }
        String suffix = matcher.group(4);
        int turns = suffix == null ? 1 : suffix.equals("2") ? 2 : 3;
        move(face, turns, depth);
    }

    public void applySequence(String sequence) {
        if (sequence == null || sequence.isBlank()) {
            return;
        }
        for (String command : sequence.trim().split("\\s+")) {
            applyMove(command);
        }
    }

    public String serialize() {
        StringBuilder state = new StringBuilder(6 * size * size);
        for (char face : FACES) {
            int[][] stickers = faces[faceIndex(face)];
            for (int[] row : stickers) {
                for (int sticker : row) {
                    state.append((char) ('0' + sticker));
                }
            }
        }
        return state.toString();
    }

    private void rotateLayer(char face, int turns, int layer) {
        for (int turn = 0; turn < turns; turn++) {
            if (layer == 0) {
                rotateFaceClockwise(face);
            }
            if (layer == size - 1) {
                rotateFaceCounterClockwise(OPPOSITES.get(face));
            }
            cycleAdjacentStickers(face, layer);
        }
    }

    private void cycleAdjacentStickers(char face, int layer) {
        int last = size - 1;
        switch (face) {
            case 'U' -> {
                int[] temp = faces[faceIndex('F')][layer].clone();
                faces[faceIndex('F')][layer] = faces[faceIndex('R')][layer].clone();
                faces[faceIndex('R')][layer] = faces[faceIndex('B')][layer].clone();
                faces[faceIndex('B')][layer] = faces[faceIndex('L')][layer].clone();
                faces[faceIndex('L')][layer] = temp;
            }
            case 'D' -> {
                int row = last - layer;
                int[] temp = faces[faceIndex('F')][row].clone();
                faces[faceIndex('F')][row] = faces[faceIndex('L')][row].clone();
                faces[faceIndex('L')][row] = faces[faceIndex('B')][row].clone();
                faces[faceIndex('B')][row] = faces[faceIndex('R')][row].clone();
                faces[faceIndex('R')][row] = temp;
            }
            case 'F' -> {
                int column = last - layer;
                int[] up = faces[faceIndex('U')][column].clone();
                int[] left = reversedColumn(faces[faceIndex('L')], column);
                int[] down = reversedRow(faces[faceIndex('D')][layer]);
                int[] right = column(faces[faceIndex('R')], layer);
                for (int i = 0; i < size; i++) {
                    faces[faceIndex('U')][column][i] = left[i];
                    faces[faceIndex('L')][last - i][column] = down[i];
                    faces[faceIndex('D')][layer][last - i] = right[i];
                    faces[faceIndex('R')][i][layer] = up[i];
                }
            }
            case 'B' -> {
                int column = last - layer;
                int[] up = reversedRow(faces[faceIndex('U')][layer]);
                int[] right = reversedColumn(faces[faceIndex('R')], column);
                int[] down = faces[faceIndex('D')][column].clone();
                int[] left = column(faces[faceIndex('L')], layer);
                for (int i = 0; i < size; i++) {
                    faces[faceIndex('U')][layer][last - i] = right[i];
                    faces[faceIndex('R')][last - i][column] = down[i];
                    faces[faceIndex('D')][column][i] = left[i];
                    faces[faceIndex('L')][i][layer] = up[i];
                }
            }
            case 'R' -> {
                int column = last - layer;
                int[] up = column(faces[faceIndex('U')], column);
                int[] front = column(faces[faceIndex('F')], column);
                int[] down = column(faces[faceIndex('D')], column);
                int[] back = reversedColumn(faces[faceIndex('B')], layer);
                for (int i = 0; i < size; i++) {
                    faces[faceIndex('U')][i][column] = front[i];
                    faces[faceIndex('F')][i][column] = down[i];
                    faces[faceIndex('D')][i][column] = back[i];
                    faces[faceIndex('B')][last - i][layer] = up[i];
                }
            }
            case 'L' -> {
                int column = last - layer;
                int[] up = column(faces[faceIndex('U')], layer);
                int[] back = reversedColumn(faces[faceIndex('B')], column);
                int[] down = column(faces[faceIndex('D')], layer);
                int[] front = column(faces[faceIndex('F')], layer);
                for (int i = 0; i < size; i++) {
                    faces[faceIndex('U')][i][layer] = back[i];
                    faces[faceIndex('B')][last - i][column] = down[i];
                    faces[faceIndex('D')][i][layer] = front[i];
                    faces[faceIndex('F')][i][layer] = up[i];
                }
            }
            default -> throw new IllegalArgumentException("Unknown face: " + face);
        }
    }

    private void rotateFaceClockwise(char face) {
        int[][] source = faces[faceIndex(face)];
        int[][] rotated = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rotated[column][size - 1 - row] = source[row][column];
            }
        }
        faces[faceIndex(face)] = rotated;
    }

    private void rotateFaceCounterClockwise(char face) {
        int[][] source = faces[faceIndex(face)];
        int[][] rotated = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rotated[size - 1 - column][row] = source[row][column];
            }
        }
        faces[faceIndex(face)] = rotated;
    }

    private static int[] column(int[][] face, int column) {
        int[] result = new int[face.length];
        for (int row = 0; row < face.length; row++) {
            result[row] = face[row][column];
        }
        return result;
    }

    private static int[] reversedColumn(int[][] face, int column) {
        int[] result = column(face, column);
        for (int start = 0, end = result.length - 1; start < end; start++, end--) {
            int temp = result[start];
            result[start] = result[end];
            result[end] = temp;
        }
        return result;
    }

    private static int[] reversedRow(int[] row) {
        int[] result = row.clone();
        for (int start = 0, end = result.length - 1; start < end; start++, end--) {
            int temp = result[start];
            result[start] = result[end];
            result[end] = temp;
        }
        return result;
    }

    private static int faceIndex(char face) {
        int index = FACES.indexOf(face);
        if (index < 0) {
            throw new IllegalArgumentException("Unknown face: " + face);
        }
        return index;
    }
}
