"""Resolve cubos 3x3 com o algoritmo de duas fases de Kociemba."""

import os
import sys

try:
    import kociemba
except ModuleNotFoundError as error:
    if error.name != "kociemba":
        raise
    raise ImportError(
        "A dependência kociemba não está instalada. "
        "Execute: py -m pip install -r requirements.txt"
    ) from error


pasta_nxn = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "NxN")
if pasta_nxn not in sys.path:
    sys.path.insert(0, pasta_nxn)

from cubo import Cubo


class Solver3x3:
    """Resolve um cubo 3x3 representado pela classe ``Cubo``."""

    def __init__(self, cubo):
        """Inicializa o solver com um cubo de dimensão 3."""
        if cubo.N != 3:
            raise ValueError("Este solver suporta apenas cubos 3x3.")
        self.cubo = cubo

    def _para_facelets(self):
        """Converte as matrizes de faces para a notação URFDLB de Kociemba."""
        cores_para_faces = {
            int(self.cubo.faces[face][1, 1]): face
            for face in ("U", "R", "F", "D", "L", "B")
        }
        if len(cores_para_faces) != 6:
            raise ValueError("Cada centro do cubo deve ter uma cor diferente.")

        contagens = {cor: 0 for cor in cores_para_faces}
        for matriz in self.cubo.faces.values():
            for cor in matriz.flat:
                cor = int(cor)
                if cor not in contagens:
                    raise ValueError("O cubo contém uma cor que não corresponde a nenhum centro.")
                contagens[cor] += 1

        if any(contagem != 9 for contagem in contagens.values()):
            raise ValueError("Um cubo 3x3 válido deve ter nove peças de cada cor.")

        return "".join(
            cores_para_faces[int(cor)]
            for face in ("U", "R", "F", "D", "L", "B")
            for cor in self.cubo.faces[face].flat
        )

    def resolver(self):
        """Devolve uma solução como lista de movimentos na notação padrão.

        Uma lista vazia indica que o cubo já está resolvido. Estados com
        combinações de peças fisicamente impossíveis são rejeitados por Kociemba.
        """
        if self.cubo.resolvido():
            return []

        facelets = self._para_facelets()
        return kociemba.solve(facelets).split()


if __name__ == "__main__":
    cubo = Cubo(3)
    scramble = "R U R' U' F2"
    for movimento in scramble.split():
        cubo.aplicar_comando(movimento)

    print(f"Scramble: {scramble}")
    solucao = Solver3x3(cubo).resolver()
    print(f"Solução ({len(solucao)} movimentos): {' '.join(solucao)}")
