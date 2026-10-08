import numpy as np

class Cubo:
    def __init__(self, n=3):
        self.N = n
        # Representação matricial eficiente com NumPy (agora com inteiros)
        self.faces = {
            'U': np.full((n, n), 0, dtype=int),
            'D': np.full((n, n), 1, dtype=int),
            'F': np.full((n, n), 2, dtype=int),
            'B': np.full((n, n), 3, dtype=int),
            'L': np.full((n, n), 4, dtype=int),
            'R': np.full((n, n), 5, dtype=int)
        }
    def resolvido(self):
        for matriz in self.faces.values():
            if not np.all(matriz == matriz[0, 0]):
                return False
        return True

    def _girar_matriz(self, face_nome):
        # A rotação por defeito no NumPy é no sentido anti-horário; usamos k=-1 para horário
        self.faces[face_nome] = np.rot90(self.faces[face_nome], k=-1)

    def _rodar_camada_especifica(self, face, vezes, d):
        N = self.N
        for _ in range(vezes):
            if d == 0:
                self._girar_matriz(face)

            # Manipulação otimizada de vetores (slicing)
            if face == 'U':
                temp = self.faces['F'][d, :].copy()
                self.faces['F'][d, :] = self.faces['R'][d, :]
                self.faces['R'][d, :] = self.faces['B'][d, :]
                self.faces['B'][d, :] = self.faces['L'][d, :]
                self.faces['L'][d, :] = temp
            elif face == 'D':
                idx = N - 1 - d
                temp = self.faces['F'][idx, :].copy()
                self.faces['F'][idx, :] = self.faces['L'][idx, :]
                self.faces['L'][idx, :] = self.faces['B'][idx, :]
                self.faces['B'][idx, :] = self.faces['R'][idx, :]
                self.faces['R'][idx, :] = temp
            elif face == 'F':
                idx = N - 1 - d
                temp = self.faces['U'][idx, :].copy()
                self.faces['U'][idx, :] = self.faces['L'][::-1, idx]
                self.faces['L'][::-1, idx] = self.faces['D'][d, ::-1]
                self.faces['D'][d, ::-1] = self.faces['R'][:, d]
                self.faces['R'][:, d] = temp
            elif face == 'B':
                idx = N - 1 - d
                temp = self.faces['U'][d, ::-1].copy()
                self.faces['U'][d, ::-1] = self.faces['R'][::-1, idx]
                self.faces['R'][::-1, idx] = self.faces['D'][idx, :]
                self.faces['D'][idx, :] = self.faces['L'][:, d]
                self.faces['L'][:, d] = temp
            elif face == 'R':
                idx = N - 1 - d
                temp = self.faces['U'][:, idx].copy()
                self.faces['U'][:, idx] = self.faces['F'][:, idx]
                self.faces['F'][:, idx] = self.faces['D'][:, idx]
                self.faces['D'][:, idx] = self.faces['B'][::-1, d]
                self.faces['B'][::-1, d] = temp
            elif face == 'L':
                idx = N - 1 - d
                temp = self.faces['U'][:, d].copy()
                self.faces['U'][:, d] = self.faces['B'][::-1, idx]
                self.faces['B'][::-1, idx] = self.faces['D'][:, d]
                self.faces['D'][:, d] = self.faces['F'][:, d]
                self.faces['F'][:, d] = temp

    def movimento(self, face, vezes=1, profundidade=1):
        profundidade = min(profundidade, self.N)
        for d in range(profundidade):
            self._rodar_camada_especifica(face, vezes, d)

    def aplicar_comando(self, comando):
        if not comando: return

        vezes = 1
        if comando.endswith("'"):
            vezes = 3
            comando = comando[:-1]
        elif comando.endswith("2"):
            vezes = 2
            comando = comando[:-1]

        wide = False
        if comando.endswith("w"):
            wide = True
            comando = comando[:-1]

        profundidade = 1
        if comando[0].isdigit():
            num_str = ""
            while comando[0].isdigit():
                num_str += comando[0]
                comando = comando[1:]
            if num_str:
                profundidade = int(num_str)
        elif wide:
            profundidade = 2

        face = comando
        if face in self.faces:
            self.movimento(face, vezes, profundidade)