# Solver 2x2

Esta pasta contém `solver.py`, que implementa `Solver2x2`. O solver usa o
modelo `Cubo` de [`../NxN/`](../NxN/README.md), normaliza a orientação do cubo
e procura uma solução com pesquisa bidirecional em largura sobre movimentos
das faces `U`, `R` e `F`.

## Executar

A partir da raiz do projeto:

```powershell
py 2x2\solver.py
```

O bloco de execução cria um cubo, aplica o scramble de exemplo definido no
ficheiro e imprime uma solução quando esta é encontrada.

## Utilização a partir da raiz como módulo

```python
import sys

sys.path.extend(["2x2", "NxN"])

from cubo import Cubo
from solver import Solver2x2

cubo = Cubo(2)
solver = Solver2x2(cubo)
solucao = solver.resolver()
```

`Solver2x2` rejeita cubos cuja dimensão não seja 2. `resolver()` devolve uma
lista de movimentos; devolve uma lista vazia se o cubo já estiver resolvido
e `None` se a pesquisa não encontrar solução.
