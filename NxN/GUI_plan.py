import tkinter as tk
import random
from tkinter import messagebox
from cubo import Cubo


class CuboGUI:
    def __init__(self, master, n):
        self.master = master
        self.master.title(f"Cubo Mágico {n}x{n}")
        self.master.minsize(400, 400)
        self.N = n
        self.cubo = Cubo(self.N)

        self.cores = {
            '0': 'white', '1': 'yellow', '2': 'green',
            '3': 'blue', '4': 'orange', '5': 'red'
        }

        self.tam_quadrado = 40
        self.tam_face = self.tam_quadrado * self.N
        self.canvas_width = 400
        self.canvas_height = 300

        # OTIMIZAÇÃO: Registo das instâncias gráficas (retângulos)
        self.retangulos_ids = {}

        # FRAME 1: Comandos Avançados
        frame_input = tk.Frame(master)
        frame_input.pack(side=tk.TOP, fill=tk.X, pady=5)
        sub_frame_input = tk.Frame(frame_input)
        sub_frame_input.pack(anchor=tk.CENTER)

        tk.Label(sub_frame_input, text="Sequência (ex: Rw U' 3Fw2):").pack(side=tk.LEFT, padx=5)
        self.entry_movimentos = tk.Entry(sub_frame_input, width=30)
        self.entry_movimentos.pack(side=tk.LEFT, padx=5)
        self.entry_movimentos.bind("<Return>", lambda event: self.executar_sequencia())
        tk.Button(sub_frame_input, text="Executar", command=self.executar_sequencia, bg="lightblue").pack(side=tk.LEFT)

        # FRAME 2: Botões Básicos
        frame_botoes = tk.Frame(master)
        frame_botoes.pack(side=tk.TOP, pady=5)

        faces = ['U', 'F', 'R', 'L', 'B', 'D']
        for i, face in enumerate(faces):
            tk.Button(frame_botoes, text=face, command=lambda f=face: self.mover(f), width=5).grid(row=i // 3,
                                                                                                   column=(i % 3) * 3,
                                                                                                   padx=2, pady=2)
            tk.Button(frame_botoes, text=f"{face}'", command=lambda f=face: self.mover(f"{f}'"), width=5).grid(
                row=i // 3, column=(i % 3) * 3 + 1, padx=2, pady=2)
            tk.Button(frame_botoes, text=f"{face}2", command=lambda f=face: self.mover(f"{f}2"), width=5).grid(
                row=i // 3, column=(i % 3) * 3 + 2, padx=10, pady=2)

        tk.Button(frame_botoes, text="Embaralhar", command=self.embaralhar, bg="lightcoral", width=15).grid(row=2,
                                                                                                            column=0,
                                                                                                            columnspan=9,
                                                                                                            pady=10)

        # ÁREA DO CUBO
        self.canvas = tk.Canvas(master, bg="gray20")
        self.canvas.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        self.canvas.bind("<Configure>", self.ao_redimensionar)

        self.embaralhar()

    def embaralhar(self):
        """Gera um scramble pseudo-aleatório direto, evitando movimentos redundantes e cancelamentos."""
        movimentos_gerados = []
        # Eixos opostos controlados
        eixos = [('U', 'D'), ('F', 'B'), ('L', 'R')]

        num_movimentos = 20 + (self.N - 2) * 10
        eixo_anterior = -1

        for _ in range(num_movimentos):
            eixo_atual = random.randint(0, 2)
            while eixo_atual == eixo_anterior:
                eixo_atual = random.randint(0, 2)

            eixo_anterior = eixo_atual
            face = random.choice(eixos[eixo_atual])
            mod = random.choice(['', "'", '2'])

            comando = face
            if self.N >= 4 and random.choice([True, False]):
                prof = random.randint(2, self.N // 2 + 1)
                comando = f"{prof}{face}w" if prof > 2 else f"{face}w"

            comando += mod
            movimentos_gerados.append(comando)

        self.cubo = Cubo(self.N)
        for mov in movimentos_gerados:
            self.cubo.aplicar_comando(mov)

        print(f"Scramble ({self.N}x{self.N}): {' '.join(movimentos_gerados)}")
        self.desenhar_cubo()

    def executar_sequencia(self):
        sequencia = self.entry_movimentos.get().strip()
        if not sequencia: return

        for mov in sequencia.split():
            self.mover(mov)

        self.entry_movimentos.delete(0, tk.END)

    def mover(self, movimento):
        self.cubo.aplicar_comando(movimento)
        self.desenhar_cubo()

        if self.cubo.resolvido():
            messagebox.showinfo("Cubo Resolvido", f"Parabéns! O cubo {self.N}x{self.N} está resolvido.")

    def ao_redimensionar(self, event):
        """Calcula o novo tamanho e recria o canvas em caso de alteração das dimensões."""
        self.canvas_width = event.width
        self.canvas_height = event.height

        max_largura_quadrado = self.canvas_width / (4 * self.N)
        max_altura_quadrado = self.canvas_height / (3 * self.N)
        self.tam_quadrado = min(max_largura_quadrado, max_altura_quadrado) * 0.95
        self.tam_face = self.tam_quadrado * self.N

        # Limpa os registos forçando recriação dos retângulos apenas no redimensionamento
        self.canvas.delete("all")
        self.retangulos_ids.clear()
        self.desenhar_cubo()

    def desenhar_face(self, face_nome, offset_x, offset_y):
        matriz = self.cubo.faces[face_nome]
        for l in range(self.N):
            for c in range(self.N):
                # Conversão explícita para string devido à compatibilidade da matriz numpy (np.str_)
                cor = self.cores[str(matriz[l, c])]
                tag = f"{face_nome}_{l}_{c}"

                # OTIMIZAÇÃO: Alterar a cor (itemconfig) se o retângulo já foi desenhado
                if tag in self.retangulos_ids:
                    self.canvas.itemconfig(self.retangulos_ids[tag], fill=cor)
                else:
                    x1 = offset_x + c * self.tam_quadrado
                    y1 = offset_y + l * self.tam_quadrado
                    x2 = x1 + self.tam_quadrado
                    y2 = y1 + self.tam_quadrado
                    rect_id = self.canvas.create_rectangle(x1, y1, x2, y2, fill=cor, outline="black", width=2)
                    self.retangulos_ids[tag] = rect_id

    def desenhar_cubo(self):
        tf = self.tam_face
        largura_total_desenho = tf * 4
        altura_total_desenho = tf * 3

        offset_x_global = (self.canvas_width - largura_total_desenho) / 2
        offset_y_global = (self.canvas_height - altura_total_desenho) / 2

        self.desenhar_face('U', offset_x_global + tf, offset_y_global)
        self.desenhar_face('L', offset_x_global, offset_y_global + tf)
        self.desenhar_face('F', offset_x_global + tf, offset_y_global + tf)
        self.desenhar_face('R', offset_x_global + tf * 2, offset_y_global + tf)
        self.desenhar_face('B', offset_x_global + tf * 3, offset_y_global + tf)
        self.desenhar_face('D', offset_x_global + tf, offset_y_global + tf * 2)


if __name__ == "__main__":
    root = tk.Tk()
    root.geometry("800x600")
    TAMANHO_N = 3
    app = CuboGUI(root, TAMANHO_N)
    root.mainloop()