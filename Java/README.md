# Solvers Speedcubing

Aplicação JavaFX para representar cubos de Rubik, aplicar sequências de movimentos e obter soluções para cubos 2×2, 3×3, 4×4 e 5×5.

## Requisitos

- JDK 23
- Maven 3.8 ou superior

O Maven descarrega as dependências JavaFX e JUnit definidas no `pom.xml`.

## Executar

Na pasta do projeto:

```bash
mvn javafx:run
```

A aplicação abre com um cubo 3×3 baralhado. Escolha o tamanho, introduza uma sequência ou use os botões de movimentos, e selecione **Find solution** para calcular uma solução. A solução é colocada no campo de sequência; prima **Execute** para a aplicar ao cubo.

O seletor permite representar cubos de 2×2 até 11×11. A resolução automática está atualmente disponível apenas para 2×2 a 5×5; os restantes tamanhos podem ser visualizados e manipulados, mas não resolvidos pela aplicação.

## Movimentos

As faces são `U`, `D`, `F`, `B`, `L` e `R`. Cada movimento roda uma camada; o sufixo `2` indica meia-volta e `'` indica um quarto de volta no sentido inverso.

```text
R U R' U'       movimentos de uma camada
Rw U' 3Fw2      duas camadas de R e três camadas de F
```

Um prefixo numérico define a profundidade, por exemplo, `3Rw` roda as três camadas junto à face `R`. Sem prefixo, um movimento largo como `Rw` roda as duas camadas exteriores.

## Solvers

| Tamanho | Implementação | Estratégia |
| --- | --- | --- |
| 2×2 | `cs.cube222` | Busca bidirecional com normalização da orientação do cubo. |
| 3×3 | `cs.min2phase` | Algoritmo min2phase de Kociemba. |
| 4×4 | `cs.threephase` | Solver TPR de três fases, incluindo redução para a fase final 3×3. |
| 5×5 | `cs.cube555` | Redução do 5×5 seguida da resolução do estado 3×3 com min2phase. |

Cada adaptador em `pt.cubesolvers.solver` valida o tamanho, converte o modelo interno para o formato esperado pelo algoritmo e devolve movimentos compatíveis com `Cube`. Os solvers trabalham sobre uma cópia do cubo recebido, sem alterar o estado original. O solver 3×3 pode devolver `Optional.empty()` quando atinge o limite de pesquisa; estados inválidos podem originar uma exceção.

O solver 5×5 cria e reutiliza tabelas de pesquisa no primeiro uso. Por omissão, o cache fica em:

```text
<diretório do utilizador>/.cubesolvers/cube555
```

É possível escolher outra pasta definindo a propriedade Java `pt.cubesolvers.cube555.cacheDir`, por exemplo:

```bash
mvn javafx:run "-Dpt.cubesolvers.cube555.cacheDir=cube555-cache"
```

A primeira inicialização pode demorar significativamente mais do que as seguintes.

## Estrutura do código

```text
src/main/java/
  pt/cubesolvers/model/       Modelo e movimentos do cubo
  pt/cubesolvers/solver/      Adaptadores dos solvers
  pt/cubesolvers/ui/          Aplicação e interface JavaFX
  cs/cube222/                 Solver 2×2
  cs/min2phase/               Solver 3×3 e fase final 3×3
  cs/threephase/              Solver 4×4
  cs/cube555/                 Redução e solver 5×5
src/test/java/                Testes do modelo e dos solvers
LICENSES/                     Licenças e avisos das implementações
```

### Módulos principais

- `pt.cubesolvers.model` — estado do cubo, validação de movimentos e serialização do estado interno.
- `pt.cubesolvers.solver` — adaptadores para 2×2, 3×3, 4×4 e 5×5, com validação da dimensão e normalização dos dados para cada solver.
- `pt.cubesolvers.ui` — aplicação JavaFX, botões de movimento, embaralhamento e resolução automática.
- `cs.*` — motores algorítmicos externos ou adaptados para cada tipo de cubo.

O modelo `pt.cubesolvers.model.Cube` usa as faces na ordem `U, D, F, B, L, R`, representadas internamente pelas cores `0` a `5`. A interface traduz estas cores para as cores visuais do cubo.

Para mais detalhes sobre os módulos internos, consulte:

- `src/main/java/pt/cubesolvers/README.md`
- `src/main/java/cs/README.md`

## Testes e compilação

Executar os testes:

```bash
mvn test
```

Criar o JAR:

```bash
mvn package
```

Os testes de integração do 5×5 podem ser demorados na primeira execução, enquanto as tabelas são inicializadas.

## Licenças

O projeto contém implementações com licenças e atribuições distintas. Consulte os ficheiros em `LICENSES/` antes de reutilizar ou redistribuir código:

- `SOLVER2X2-MIT.txt` — solver 2×2, copyright de Ivo Rosa.
- `MIN2PHASE-MIT.txt` — min2phase, de Chen Shuang.
- `TPR-4X4-MIT.txt` — TPR-4x4x4-Solver, de Chen Shuang.
- `CUBE555-MIT.txt` — cube555, de Chen Shuang.
