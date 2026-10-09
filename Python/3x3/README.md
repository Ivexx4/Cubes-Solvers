# Solver 3x3

`solver.py` implementa `Solver3x3`, que converte o estado do modelo
[`Cubo`](../NxN/README.md) para a notação de faces URFDLB e usa uma
implementação Python pura do algoritmo de duas fases de Kociemba para encontrar
uma solução. O módulo verifica a dimensão, os centros e a quantidade de cada
cor; estados que não correspondam a um cubo fisicamente válido são rejeitados
pelo solver.

## Instalação e execução

Na raiz do projeto, instale as dependências:

```powershell
py -m pip install -r requirements.txt
```

Execute o exemplo incluído:

```powershell
py 3x3\solver.py
```

O exemplo aplica um scramble ao cubo resolvido e imprime a sequência
encontrada.

## Utilização

```python
import sys

sys.path.extend(["3x3", "NxN"])

from cubo import Cubo
from solver import Solver3x3

cubo = Cubo(3)
for movimento in "R U R' U' F2".split():
    cubo.aplicar_comando(movimento)

solucao = Solver3x3(cubo).resolver()
print(" ".join(solucao))
```

`resolver()` devolve uma lista de movimentos na notação padrão (`R`, `U'`,
`F2`, etc.), ou `None` se a pesquisa exceder o limite de tempo ou de
comprimento. Por omissão, a pesquisa tem um limite de 30 segundos e procura
soluções até 22 movimentos; estes limites podem ser configurados ao criar
`Solver3x3(cubo, max_length=..., timeout=...)`. A primeira execução pode ser
demorada, pois gera tabelas de pesquisa que são guardadas em cache para as
execuções seguintes. O solver não altera o cubo recebido; aplique a lista ao
cubo com `cubo.aplicar_comando(movimento)` se quiser executar a solução.
