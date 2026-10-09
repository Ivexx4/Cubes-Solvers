/*
 * Copyright (c) 2026 Ivo Rosa
 * Licensed under the MIT License. See LICENSES/SOLVER2X2-MIT.txt.
 */
package pt.cubesolvers.solver;

import cs.cube222.Search;
import pt.cubesolvers.model.Cube;

import java.util.List;
import java.util.Optional;

public final class Solver2x2 {
    private final Cube cube;

    public Solver2x2(Cube cube) {
        if (cube.size() != 2) {
            throw new IllegalArgumentException("This solver supports only 2x2 cubes.");
        }
        this.cube = cube.copy();
    }

    public Optional<List<String>> solve() {
        return new Search(cube).solve();
    }
}
