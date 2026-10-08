# Cube Solvers

Projeto Python para representar cubos de Rubik NxN, aplicar movimentos e
visualizar/manipular o cubo numa interface gráfica. Inclui também um solver
por pesquisa bidirecional para cubos 2x2.

## Conteúdo

- [`2x2/`](2x2/README.md): solver 2x2 baseado em pesquisa em largura.
- [`3x3/`](3x3/README.md): solver 3x3 pelo algoritmo de duas fases de Kociemba.
- [`NxN/`](NxN/README.md): modelo do cubo e interface gráfica para NxN.

## Requisitos

- Python 3.
- NumPy (`numpy`).
- Tkinter, normalmente incluído nas instalações de Python para desktop.

Instale as dependências a partir da raiz do projeto:

```powershell
py -m pip install -r requirements.txt
```

## Executar

Iniciar a interface gráfica (por omissão, 3x3):

```powershell
py NxN\GUI_plan.py
```

Executar o exemplo do solver 2x2:

```powershell
py 2x2\solver.py
```

Executar o exemplo do solver 3x3:

```powershell
py 3x3\solver.py
```

O solver 2x2 aceita apenas cubos dessa dimensão. A interface gráfica permite
escolher outra dimensão alterando `TAMANHO_N` no final de `NxN/GUI_plan.py`.

## Notação de movimentos

As faces seguem a notação padrão: `U` (cima), `D` (baixo), `F` (frente),
`B` (trás), `L` (esquerda) e `R` (direita). Sem sufixo, o movimento é horário;
`'` indica anti-horário e `2` meia-volta. `Rw` roda as duas camadas da direita,
e um prefixo numérico define a profundidade, por exemplo `3Fw2`.

## Estado atual

O modelo e a GUI NxN estão em `NxN/`; os solvers específicos estão nas
pastas `2x2/` e `3x3/`.
