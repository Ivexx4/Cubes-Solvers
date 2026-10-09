/*
 * Copyright (c) 2026 Ivo Rosa
 * Licensed under the MIT License. See LICENSES/SOLVER2X2-MIT.txt.
 */
package cs.cube222;

import pt.cubesolvers.model.Cube;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class CubeOrientation {
    private static final List<String> ROTATIONS =
            List.of("Uw", "Uw'", "Uw2", "Rw", "Rw'", "Rw2", "Fw", "Fw'", "Fw2");

    private final Cube cube;
    private final List<String> rotations;

    private CubeOrientation(Cube cube, List<String> rotations) {
        this.cube = cube;
        this.rotations = rotations;
    }

    static CubeOrientation normalize(Cube original) {
        Deque<Node> queue = new ArrayDeque<>();
        Map<String, Boolean> visited = new HashMap<>();
        queue.add(new Node(original.copy(), List.of()));
        visited.put(original.serialize(), true);

        while (!queue.isEmpty()) {
            Node node = queue.removeFirst();
            Cube cube = node.cube();
            if (isCanonical(cube)) {
                return new CubeOrientation(cube, node.rotations());
            }
            for (String rotation : ROTATIONS) {
                Cube rotated = cube.copy();
                rotated.applyMove(rotation);
                if (visited.putIfAbsent(rotated.serialize(), true) == null) {
                    queue.addLast(new Node(rotated, append(node.rotations(), rotation)));
                }
            }
        }
        throw new IllegalStateException("Could not normalize the cube orientation.");
    }

    Cube cube() {
        return cube.copy();
    }

    List<String> restoreFaceNames(List<String> normalizedMoves) {
        Map<Character, Character> faceMap = new HashMap<>();
        for (char face : new char[]{'U', 'D', 'F', 'B', 'L', 'R'}) {
            faceMap.put(face, face);
        }
        for (String rotation : rotations) {
            char axis = rotation.charAt(0);
            int turns = rotation.endsWith("'") ? 3 : rotation.endsWith("2") ? 2 : 1;
            for (int i = 0; i < turns; i++) {
                switch (axis) {
                    case 'U' -> cycle(faceMap, 'F', 'L', 'B', 'R');
                    case 'R' -> cycle(faceMap, 'U', 'B', 'D', 'F');
                    case 'F' -> cycle(faceMap, 'U', 'R', 'D', 'L');
                    default -> throw new IllegalStateException("Unexpected rotation axis.");
                }
            }
        }
        List<String> restored = new ArrayList<>(normalizedMoves.size());
        for (String move : normalizedMoves) {
            restored.add(faceMap.get(move.charAt(0)) + move.substring(1));
        }
        return List.copyOf(restored);
    }

    private static boolean isCanonical(Cube cube) {
        return cube.colorAt('D', 1, 0) == 1
                && cube.colorAt('B', 1, 1) == 3
                && cube.colorAt('L', 1, 0) == 4;
    }

    private static void cycle(Map<Character, Character> map, char a, char b, char c, char d) {
        Character oldA = map.get(a);
        map.put(a, map.get(d));
        map.put(d, map.get(c));
        map.put(c, map.get(b));
        map.put(b, oldA);
    }

    private static List<String> append(List<String> path, String move) {
        List<String> result = new ArrayList<>(path.size() + 1);
        result.addAll(path);
        result.add(move);
        return List.copyOf(result);
    }

    private record Node(Cube cube, List<String> rotations) {
    }
}
