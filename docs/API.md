# Documentação de API

Este documento resume a API pública dos módulos mais importantes do projeto.

## Java

### `pt.cubesolvers.model.Cube`

#### Construtores

- `Cube()`
  - cria um cubo 3×3 resolvido.
- `Cube(int size)`
  - cria um cubo `size x size` resolvido.

#### Métodos principais

- `int size()`
  - devolve a dimensão do cubo.
- `int colorAt(char face, int row, int column)`
  - devolve a cor do sticker na posição indicada.
- `Cube copy()`
  - retorna uma cópia independente do cubo atual.
- `boolean isSolved()`
  - verifica se o cubo está resolvido.
- `void move(char face, int turns, int depth)`
  - aplica uma sequência de rotações numa face e profundidade dadas.
- `void applyMove(String command)`
  - interpreta um comando do tipo `R`, `U'`, `3Rw2`, `F2`.
- `void applySequence(String sequence)`
  - aplica vários comandos separados por espaços.
- `String serialize()`
  - devolve uma representação em texto do estado do cubo.

#### Regras de notação

- `face`: `U`, `D`, `F`, `B`, `L`, `R`
- `turns`: `1`, `2`, `3`
- `depth`: número da camada, entre `1` e `size`

Exemplo:

```java
Cube cube = new Cube(3);
cube.applySequence("R U R' U'");
```

### `pt.cubesolvers.solver.Solver2x2`

#### Construtor

- `Solver2x2(Cube cube)`

#### Método

- `Optional<List<String>> solve()`
  - resolve o cubo 2×2 e devolve uma lista de movimentos, ou `Optional.empty()` se não for possível resolver.

### `pt.cubesolvers.solver.Solver3x3`

#### Construtor

- `Solver3x3(Cube cube)`
- `Solver3x3(Cube cube, int maxDepth, long timeoutSeconds)`

#### Método

- `Optional<List<String>> solve()`
  - resolve um cubo 3×3 usando `min2phase`.

### `pt.cubesolvers.solver.Solver4x4`

#### Construtor

- `Solver4x4(Cube cube)`

#### Método

- `Optional<List<String>> solve()`
  - resolve cubos 4×4 com o solver TPR.

### `pt.cubesolvers.solver.Solver5x5`

#### Construtor

- `Solver5x5(Cube cube)`

#### Método

- `Optional<List<String>> solve()`
  - reduz o cubo 5×5 e resolve a parte final com a fase 3×3.

## Python

### `Python/NxN/cubo.py: Cubo`

#### Construtor

- `Cubo(n=3)`

#### Métodos principais

- `resolvido()`
  - devolve `True` se cada face tiver uma cor uniforme.
- `movimento(face, vezes=1, profundidade=1)`
  - aplica um movimento numa face e camada indicada.
- `aplicar_comando(comando)`
  - interpreta comandos como:
    - `R`
    - `U'`
    - `F2`
    - `Rw`
    - `3Fw2`

#### Observação

A classe suporta a notação de faces e a sintaxe de movimentos do cubo, tornando-se a base para os solvers.

### `Python/2x2/solver.py: Solver2x2`

#### Construtor

- `Solver2x2(cubo_inicial)`

#### Método

- `resolver()`
  - devolve uma lista de movimentos para resolver o cubo 2×2;
  - devolve lista vazia se o cubo já estiver resolvido;
  - devolve `None` se não encontrar solução.

### `Python/3x3/solver.py: Solver3x3`

#### Construtor

- `Solver3x3(cubo, max_length=22, timeout=30.0)`

#### Método

- `resolver()`
  - converte o cubo para facelets e usa o algoritmo de duas fases;
  - devolve a solução como lista de strings;
  - devolve `None` se exceder o limite de busca.

## Exemplos de uso

### Java

```java
Cube cube = new Cube(3);
cube.applySequence("R U R' U'");
Optional<List<String>> solution = new Solver3x3(cube).solve();
```

### Python

```python
import sys
sys.path.extend(["2x2", "NxN"])

from cubo import Cubo
from solver import Solver2x2

cubo = Cubo(2)
solver = Solver2x2(cubo)
solucao = solver.resolver()
print(solucao)
```

## Convenções

- As faces são sempre representadas por `U, D, F, B, L, R`.
- As sequências de movimento seguem a notação comum de cubo.
- O output dos solvers costuma ser uma lista de movimentos em texto simples, fácil de aplicar ao modelo original.

## Limitações conhecidas

- 5×5 em Java pode demorar mais na primeira execução por causa do carregamento de tabelas cacheadas.
- O algoritmo Python 3×3 depende da biblioteca `twophase` e pode exigir instalação explícita.
- A GUI Tkinter não substitui a aplicação JavaFX em termos de interatividade e resolução avançada.

## Conclusão

A API central do projeto é simples e direta: criar cubo, aplicar movimentos, validar estado e invocar solver. Essa padronização facilita a troca de implementações e a comparação entre certos algoritmos e interfaces.
