# Módulo de motores de resolução

Este pacote reúne as implementações de busca e redução de estado usadas pela aplicação Java para resolver cubos de vários tamanhos.

## Estrutura

```text
cs/
├── cube222/          # solver para 2×2
├── min2phase/        # algoritmo min2phase para 3×3
├── threephase/       # solver TPR para 4×4
├── cube555/          # redução do 5×5 e finalização 3×3
└── README.md         # documentação deste conjunto de algoritmos
```

## 1. `cube222`

O pacote `cs.cube222` contém a implementação específica para cubos 2×2.

### Objetivo

- resolver rapidamente estados 2×2;
- usar uma abordagem de busca bidirecional com normalização da orientação;
- reduzir o espaço de estados para uma procura leve e eficiente.

### Classes principais

- `Search` — motor principal da pesquisa;
- `TwoWaySearch` — variante de busca por dois sentidos;
- `CubeOrientation` — gestão da orientação do cubo em referência.

## 2. `min2phase`

O pacote `cs.min2phase` reúne a implementação do algoritmo de duas fases de Kociemba para cubos 3×3.

### Objetivo

- transformar o estado em facelets ou coordenadas internas;
- executar a pesquisa de solução em um espaço reduzido;
- devolver uma sequência compatível com a notação do cubo Java.

### Classes principais

- `Search` — ponto de entrada da busca;
- `CoordCube` — representação de coordenadas do cubo;
- `CubieCube` — estado interno das peças;
- `Tools` e `Util` — utilitários e operações auxiliares.

Este é o núcleo de resolução do `3×3` da aplicação.

## 3. `threephase`

O pacote `cs.threephase` implementa um solver TPR para cubos 4×4.

### Objetivo

- reduzir o estado do cubo 4×4 para fases intermedárias;
- resolver a fase final e a fase de orientação/decorrelação;
- suportar o modelo de cubo usado pela aplicação Java.

### Classes principais

- `Search` — mecanismo de busca principal;
- `CenterCube`, `CornerCube`, `EdgeCube`, `FullCube` — representações estruturadas;
- `Moves` — geração de movimentos e permutações.

## 4. `cube555`

O pacote `cs.cube555` implementa a resolução do 5×5.

### Objetivo

- reduzir o cubo 5×5 para uma forma 3×3 equivalente;
- usar uma fase de redução e depois completar o estado final com o solver 3×3;
- reutilizar tabelas e caches para acelerar buscas repetidas.

### Classes relevantes

- `Search` — launch da redução e solução;
- `Phase1Search`, `Phase2Search`, `Phase3Search`, `Phase4Search`, `Phase5Search` — fases da resolução;
- `SolvingCube` e `Tools` — suporte da estrutura e das operações do cubo;
- `Search.init()` — inicialização das tabelas de cache necessárias.

## Observações

- Os pacotes `cs.*` são motores algorítmicos, não GUI.
- Eles dão suporte ao projeto principal e encapsulam implementações de busca altamente especializadas.
- O comportamento depende de reduções de estado, facelets e tabelas pré-computadas, por isso a primeira execução pode ser mais lenta.

## Origem e licenças

Estes motores incluem implementações de terceiros e vêm com licenças próprias em `Java/LICENSES/`.

Consulte os ficheiros da pasta `LICENSES` antes de redistribuir ou reutilizar os algoritmos em outros projetos.
