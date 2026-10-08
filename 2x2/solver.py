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

        # OTIMIZAÇÃO: Acesso direto apenas a U, R, F (fixando DBL)
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
        """Clone direto de arrays NumPy."""
        novo_cubo = Cubo(2)
        for face, matriz in cubo_origem.faces.items():
            novo_cubo.faces[face] = matriz.copy()
        return novo_cubo

    def _orientar_cubo(self, cubo):
        """
        Encontra a rotação do cubo (usando movimentos wide) que coloca
        o canto DBL (cores: D=1, B=3, L=4) na sua posição e orientação fixas.
        """
        q = deque([(self._clonar_cubo(cubo), [])])
        visitados = {self._estado_para_bytes(cubo)}

        rotacoes = ['Uw', "Uw'", 'Uw2', 'Rw', "Rw'", 'Rw2', 'Fw', "Fw'", 'Fw2']

        while q:
            c_atual, caminho = q.popleft()

            if (c_atual.faces['D'][1, 0] == 1 and
                    c_atual.faces['B'][1, 1] == 3 and
                    c_atual.faces['L'][1, 0] == 4):
                return c_atual, caminho

            for rot in rotacoes:
                c_novo = self._clonar_cubo(c_atual)
                c_novo.aplicar_comando(rot)
                est = self._estado_para_bytes(c_novo)

                if est not in visitados:
                    visitados.add(est)
                    q.append((c_novo, caminho + [rot]))

        return cubo, []

    def _transpor_solucao(self, solucao_normalizada, rotacoes):
        """
        Traduz os movimentos da solução de volta para o referencial
        do scramble original, eliminando as rotações do cubo.
        """
        mapa = {'U': 'U', 'D': 'D', 'F': 'F', 'B': 'B', 'L': 'L', 'R': 'R'}

        for rot in rotacoes:
            eixo = rot[0]
            vezes = 1
            if "'" in rot:
                vezes = 3
            elif "2" in rot:
                vezes = 2

            for _ in range(vezes):
                if eixo == 'U':
                    mapa['F'], mapa['L'], mapa['B'], mapa['R'] = mapa['R'], mapa['F'], mapa['L'], mapa['B']
                elif eixo == 'R':
                    mapa['U'], mapa['B'], mapa['D'], mapa['F'] = mapa['F'], mapa['U'], mapa['B'], mapa['D']
                elif eixo == 'F':
                    mapa['U'], mapa['R'], mapa['D'], mapa['L'] = mapa['L'], mapa['U'], mapa['R'], mapa['D']

        solucao_final = []
        for mov in solucao_normalizada:
            face = mov[0]
            modificador = mov[1:] if len(mov) > 1 else ""
            solucao_final.append(mapa[face] + modificador)

        return solucao_final

    def resolver(self):
        if self.cubo_inicial.resolvido():
            return []

        # 1. Normalizar a orientação do cubo (Fixar o canto DBL)
        cubo_normalizado, rotacoes_iniciais = self._orientar_cubo(self.cubo_inicial)

        # Instanciar o estado alvo (Cubo resolvido)
        cubo_alvo = Cubo(2)

        # 2. Iniciar o BFS com o cubo já orientado corretamente
        q_ida = deque([(cubo_normalizado, [])])
        q_volta = deque([(cubo_alvo, [])])

        visitados_ida = {self._estado_para_bytes(cubo_normalizado): []}
        visitados_volta = {self._estado_para_bytes(cubo_alvo): []}

        limite_profundidade = 6

        while q_ida and q_volta:
            # ---------------------------------------------------------
            # FASE 1: Expansão da Ida
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

                    if estado_bytes in visitados_volta:
                        caminho_volta_invertido = [self.inversos[m] for m in reversed(visitados_volta[estado_bytes])]
                        solucao_bruta = novo_caminho + caminho_volta_invertido
                        return self._transpor_solucao(solucao_bruta, rotacoes_iniciais)

                    if estado_bytes not in visitados_ida:
                        visitados_ida[estado_bytes] = novo_caminho
                        q_ida.append((novo_cubo, novo_caminho))

            # ---------------------------------------------------------
            # FASE 2: Expansão da Volta
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

                    if estado_bytes_v in visitados_ida:
                        caminho_volta_invertido = [self.inversos[m] for m in reversed(novo_caminho_v)]
                        solucao_bruta = visitados_ida[estado_bytes_v] + caminho_volta_invertido
                        return self._transpor_solucao(solucao_bruta, rotacoes_iniciais)

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

    scramble = "U2 F2 L' F D' R F' D' B D2 F' D L U L U' L2 F' U2 F"
    print(f"A aplicar scramble: {scramble}")

    for movimento in scramble.split():
        meu_cubo.aplicar_comando(movimento)

    print("A procurar a solução ótima com BFS Bidirecional e normalização de eixos...")

    solver = Solver2x2(meu_cubo)
    solucao = solver.resolver()

    if solucao is not None:
        print(f"Cubo resolvido em {len(solucao)} movimentos ótimos!")
        print(f"Solução HTM transcrita para o referencial original: {' '.join(solucao)}")
    else:
        print("Não foi possível encontrar uma solução.")