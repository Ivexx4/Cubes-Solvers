import sys
import os
from collections import deque

pasta_atual = os.path.dirname(os.path.abspath(__file__))
pasta_pai = os.path.dirname(pasta_atual)
pasta_paralela = os.path.join(pasta_pai, 'NxN')
sys.path.append(pasta_paralela)

from cubo import Cubo


class Solver2x2:
    def __init__(self, cubo_inicial):
        if cubo_inicial.N != 2:
            raise ValueError("Este solver suporta apenas a versão 2x2.")
        self.cubo_inicial = cubo_inicial

        # OTIMIZAÇÃO: Acesso direto
        todos = ['U', "U'", 'U2', 'R', "R'", 'R2', 'F', "F'", 'F2']
        self.transicoes_validas = {
            None: todos,
            'U': [m for m in todos if m[0] != 'U'],
            'R': [m for m in todos if m[0] != 'R'],
            'F': [m for m in todos if m[0] != 'F']
        }

        self.args_movimento = {
            'U': ('U', 1), "U'": ('U', 3), 'U2': ('U', 2),
            'R': ('R', 1), "R'": ('R', 3), 'R2': ('R', 2),
            'F': ('F', 1), "F'": ('F', 3), 'F2': ('F', 2)
        }

        self.inversos = {
            'U': "U'", "U'": 'U', 'U2': 'U2',
            'R': "R'", "R'": 'R', 'R2': 'R2',
            'F': "F'", "F'": 'F', 'F2': 'F2'
        }

    def _estado_para_bytes(self, cubo):
        """Serialização ultrarrápida do estado atual."""
        f = cubo.faces
        return (f['U'].tobytes() + f['D'].tobytes() + f['F'].tobytes() +
                f['B'].tobytes() + f['L'].tobytes() + f['R'].tobytes())

    def _clonar_cubo(self, cubo_origem):
        """
        No BFS precisamos de manter estados em memória.
        Clonar diretamente os arrays NumPy é ordens de grandeza
        mais rápido do que usar o módulo copy (deepcopy).
        """
        novo_cubo = Cubo(2)
        for face, matriz in cubo_origem.faces.items():
            novo_cubo.faces[face] = matriz.copy()
        return novo_cubo

    def resolver(self):
        if self.cubo_inicial.resolvido():
            return []

        # Instanciar o estado alvo (Cubo resolvido)
        cubo_alvo = Cubo(2)

        # Filas para expansão: guardam tuplos (Estado_Cubo, Caminho)
        q_ida = deque([(self._clonar_cubo(self.cubo_inicial), [])])
        q_volta = deque([(cubo_alvo, [])])

        # Tabelas de Transposição (Visitados)
        # Mapeamento: { estado_em_bytes: caminho_para_chegar_lá }
        visitados_ida = {self._estado_para_bytes(self.cubo_inicial): []}
        visitados_volta = {self._estado_para_bytes(cubo_alvo): []}

        # O God's Number do 2x2 é 11 HTM. Se cada lado explorar até profundidade 6,
        # cruzam-se garantidamente a meio.
        limite_profundidade = 6

        while q_ida and q_volta:
            # ---------------------------------------------------------
            # FASE 1: Expansão da Ida (A partir do Scramble)
            # ---------------------------------------------------------
            cubo_atual, caminho_ida = q_ida.popleft()

            if len(caminho_ida) < limite_profundidade:
                ultimo_mov = caminho_ida[-1][0] if caminho_ida else None

                for mov in self.transicoes_validas[ultimo_mov]:
                    novo_cubo = self._clonar_cubo(cubo_atual)
                    face, vezes = self.args_movimento[mov]
                    novo_cubo.movimento(face, vezes, 1)

                    novo_caminho = caminho_ida + [mov]
                    estado_bytes = self._estado_para_bytes(novo_cubo)

                    # Intersecção encontrada!
                    if estado_bytes in visitados_volta:
                        caminho_volta_invertido = [self.inversos[m] for m in reversed(visitados_volta[estado_bytes])]
                        return novo_caminho + caminho_volta_invertido

                    if estado_bytes not in visitados_ida:
                        visitados_ida[estado_bytes] = novo_caminho
                        q_ida.append((novo_cubo, novo_caminho))

            # ---------------------------------------------------------
            # FASE 2: Expansão da Volta (A partir da Solução)
            # ---------------------------------------------------------
            cubo_atual_volta, caminho_volta = q_volta.popleft()

            if len(caminho_volta) < limite_profundidade:
                ultimo_mov_volta = caminho_volta[-1][0] if caminho_volta else None

                for mov in self.transicoes_validas[ultimo_mov_volta]:
                    novo_cubo_v = self._clonar_cubo(cubo_atual_volta)
                    face, vezes = self.args_movimento[mov]
                    novo_cubo_v.movimento(face, vezes, 1)

                    novo_caminho_v = caminho_volta + [mov]
                    estado_bytes_v = self._estado_para_bytes(novo_cubo_v)

                    # Intersecção encontrada!
                    if estado_bytes_v in visitados_ida:
                        caminho_volta_invertido = [self.inversos[m] for m in reversed(novo_caminho_v)]
                        return visitados_ida[estado_bytes_v] + caminho_volta_invertido

                    if estado_bytes_v not in visitados_volta:
                        visitados_volta[estado_bytes_v] = novo_caminho_v
                        q_volta.append((novo_cubo_v, novo_caminho_v))

        return None


# ==========================================
# BLOCO DE EXECUÇÃO E TESTE
# ==========================================
if __name__ == "__main__":
    print("A inicializar o cubo 2x2...")
    meu_cubo = Cubo(2)

    scramble = "U R2 F U' R' U' R2 U' F U F"
    print(f"A aplicar scramble: {scramble}")

    for movimento in scramble.split():
        meu_cubo.aplicar_comando(movimento)

    print("A procurar a solução ótima com BFS Bidirecional...")

    solver = Solver2x2(meu_cubo)
    solucao = solver.resolver()

    if solucao is not None:
        print(f"Cubo resolvido em {len(solucao)} movimentos ótimos!")
        print(f"Solução HTM: {' '.join(solucao)}")
    else:
        print("Não foi possível encontrar uma solução.")