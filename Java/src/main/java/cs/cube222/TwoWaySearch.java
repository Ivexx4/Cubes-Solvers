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

final class TwoWaySearch {
    private static final List<String> MOVES =
            List.of("U", "U'", "U2", "R", "R'", "R2", "F", "F'", "F2");

    private final Cube initialCube;

    TwoWaySearch(Cube cube) {
        initialCube = cube.copy();
    }

    List<String> solve() {
        Cube target = new Cube(2);
        Deque<Node> fromStart = new ArrayDeque<>();
        Deque<Node> fromTarget = new ArrayDeque<>();
        Map<String, List<String>> seenStart = new HashMap<>();
        Map<String, List<String>> seenTarget = new HashMap<>();

        fromStart.add(new Node(initialCube, List.of()));
        fromTarget.add(new Node(target, List.of()));
        seenStart.put(initialCube.serialize(), List.of());
        seenTarget.put(target.serialize(), List.of());

        while (!fromStart.isEmpty() && !fromTarget.isEmpty()) {
            List<String> solution = expand(
                    fromStart.removeFirst(), fromStart, seenStart, seenTarget, true);
            if (solution != null) {
                return solution;
            }

            solution = expand(
                    fromTarget.removeFirst(), fromTarget, seenTarget, seenStart, false);
            if (solution != null) {
                return solution;
            }
        }
        throw new IllegalStateException("The 2x2 search exhausted the reachable cube states.");
    }

    private List<String> expand(
            Node node,
            Deque<Node> queue,
            Map<String, List<String>> visited,
            Map<String, List<String>> otherVisited,
            boolean expandingFromStart) {
        if (node.path().size() >= 6) {
            return null;
        }

        Character previousFace = node.path().isEmpty() ? null : node.path().getLast().charAt(0);
        for (String move : MOVES) {
            if (previousFace != null && move.charAt(0) == previousFace) {
                continue;
            }
            Cube next = node.cube().copy();
            next.applyMove(move);
            String key = next.serialize();
            if (visited.containsKey(key)) {
                continue;
            }

            List<String> path = append(node.path(), move);
            List<String> otherPath = otherVisited.get(key);
            if (otherPath != null) {
                List<String> raw = new ArrayList<>();
                if (expandingFromStart) {
                    raw.addAll(path);
                    appendInverse(raw, otherPath);
                } else {
                    raw.addAll(otherPath);
                    appendInverse(raw, path);
                }
                return List.copyOf(raw);
            }
            visited.put(key, path);
            queue.addLast(new Node(next, path));
        }
        return null;
    }

    private static List<String> append(List<String> path, String move) {
        List<String> result = new ArrayList<>(path.size() + 1);
        result.addAll(path);
        result.add(move);
        return List.copyOf(result);
    }

    private static String inverse(String move) {
        if (move.endsWith("2")) {
            return move;
        }
        return move.endsWith("'") ? move.substring(0, move.length() - 1) : move + "'";
    }

    private static void appendInverse(List<String> result, List<String> path) {
        for (int i = path.size() - 1; i >= 0; i--) {
            result.add(inverse(path.get(i)));
        }
    }

    private record Node(Cube cube, List<String> path) {
    }
}
