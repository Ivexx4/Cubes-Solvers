"""Resolve cubos 3x3 com o algoritmo de duas fases de Kociemba."""

import os
import sys
from math import isfinite

try:
    from twophase import solve as resolver_duas_fases
except ModuleNotFoundError as error:
    if error.name != "twophase":
        raise
    raise ImportError(
        "A dependência twophase não está instalada. "
        "Execute: py -m pip install -r requirements.txt"
    ) from error


pasta_nxn = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "NxN")
if pasta_nxn not in sys.path:
    sys.path.insert(0, pasta_nxn)

from cubo import Cubo


class Solver3x3:
    """Resolve um cubo 3x3 representado pela classe ``Cubo``."""

    def __init__(self, cubo, max_length=22, timeout=30.0):
        """Inicializa o solver com limites máximos para comprimento e pesquisa."""
        if cubo.N != 3:
            raise ValueError("Este solver suporta apenas cubos 3x3.")
        if not isinstance(max_length, int) or isinstance(max_length, bool) or max_length < 1:
            raise ValueError("max_length deve ser pelo menos 1.")
        if (
            not isinstance(timeout, (int, float))
            or isinstance(timeout, bool)
            or not isfinite(timeout)
            or timeout <= 0
        ):
            raise ValueError("timeout deve ser maior que zero.")
        self.cubo = cubo
        self.max_length = max_length
        self.timeout = timeout

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
        combinações de peças fisicamente impossíveis são rejeitados pelo parser.
        """
        facelets = self._para_facelets()
        if self.cubo.resolvido():
            return []

        solucao = resolver_duas_fases(
            facelets,
            max_length=self.max_length,
            timeout=self.timeout,
        )
        return None if solucao is None else solucao.split()


if __name__ == "__main__":
    cubo = Cubo(3)
    scramble = "U2 R2 D2 B2 L'R2 D2 R2 U' L' D U2 B' D' B' F' L' F U2 R' F' D' L2 B2 U2 F' D' L2 B2 U' L2 F' D' R2 F2 L2 D' R2 F B2 U' L2 F' D' R2 F2 L2 D' R2 F B2 U' L2 F' D' R2 F2 L2 D' R2 F B2 U' L2 F' D' R2 F2 L2 D' R2 F B2 U' L2 F' D' R2 F2 L2 D' R2 F B2 U' L2 F' D' R2 F2 L2 D' R2 F B"
    for movimento in scramble.split():
        cubo.aplicar_comando(movimento)

    print(f"Scramble: {scramble}")
    solucao = Solver3x3(cubo).resolver()
    if solucao is None:
        print("Não foi encontrada solução dentro dos limites configurados.")
    else:
        print(f"Solução ({len(solucao)} movimentos): {' '.join(solucao)}")
