# Roadmap WCA

## Objetivo

Cobrir o conjunto de puzzles oficiais da WCA com uma implementação principal em Java, usando a arquitetura atual como base para expansão gradual.

## Estado atual

- Java: implementação principal e ativa
- Python: legado, não usado em desenvolvimento futuro
- suporte atual focado em cubos 2×2 a 5×5
- base preparada para evoluir para puzzles adicionais da WCA

## Fase 1 — cubos clássicos

Prioridade principal:

- 2×2×2
- 3×3×3
- 4×4×4
- 5×5×5

Objetivos:

- estabilizar a representação do cubo em Java;
- garantir velocidade e consistência da resolução;
- validar a UI com manipulação e busca de soluções;
- cobrir casos de input inválido e estados impossíveis.

## Fase 2 — cubos grandes

Prioridade:

- 6×6×6
- 7×7×7

Objetivos:

- adaptar a lógica de camadas e redução de estados;
- gerir a maior complexidade das peças e a memória necessária;
- integrar algoritmos específicos de redução para grandes cubos.

## Fase 3 — puzzles de baixa ordem e não clássicos

Prioridade:

- Pyraminx
- Skewb
- Megaminx
- Clock
- Square-1

Objetivos:

- modelar a geometria e a notação específicas de cada puzzle;
- desenvolver solvers próprios ou adaptar motores terceiros;
- unificar a camada visual e a camada de movimento.

## Fase 4 — variantes competitivas e resolução avançada

A incluir conforme a maturidade do projeto:

- 3×3 blindfolded
- 4×4 blindfolded
- 5×5 blindfolded
- fewest moves
- múltiplas estratégias de resolução por puzzle

## Critérios de aceitação do roadmap

Todas as fases devem garantir:

- representação correta do estado do puzzle;
- notação consistente e exportável;
- testes para estados resolvidos, embaralhados e inválidos;
- interface gráfica funcional para manipulação e visualização;
- performance aceitável para uso interativo.

## Recomendação de arquitetura

A implementação Java deve continuar a ser o eixo principal, com uma divisão clara em:

- `model`: estrutura do puzzle e movimentos;
- `solver`: adaptadores e motores de busca;
- `ui`: visualização e interação;
- `algorithms`: implementações especializadas por puzzle.

A pasta Python deve permanecer como referência histórica e não como infraestrutura de produção.

## Conclusão

O projeto já concluiu a transição da implementação principal para Java. O próximo passo é evoluir a base atual para cobrir todo o universo de puzzles da WCA, começando pelos cubos clássicos e estendendo em etapas para os restantes puzzles oficiais.
