/*
 * Copyright (c) 2026 Ivo Rosa
 * Licensed under the MIT License. See LICENSES/SOLVER2X2-MIT.txt.
 */
package cs.cube222;

import pt.cubesolvers.model.Cube;

import java.util.List;
import java.util.Optional;

public final class Search {
    private final Cube initialCube;

    public Search(Cube cube) {
        if (cube.size() != 2) {
            throw new IllegalArgumentException("This solver supports only 2x2 cubes.");
        }
        initialCube = cube.copy();
    }

    public Optional<List<String>> solve() {
        if (initialCube.isSolved()) {
            return Optional.of(List.of());
        }

        CubeOrientation orientation = CubeOrientation.normalize(initialCube);
        List<String> normalizedSolution = new TwoWaySearch(orientation.cube()).solve();
        return Optional.of(orientation.restoreFaceNames(normalizedSolution));
    }
}
