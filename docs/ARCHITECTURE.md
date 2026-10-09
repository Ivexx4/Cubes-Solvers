# Arquitetura do projeto

Este documento explica a organização do repositório e o papel de cada camada no sistema de resolução de cubos.

## Visão geral

O projeto está organizado em duas famílias principais de componentes:

1. Implementação principal em Java, ativa e orientada ao objetivo final da WCA
2. Referência legada em Python, mantida apenas por histórico e por experiências de desenvolvimento anteriores

A separação foi feita para permitir:

- visualização interativa em JavaFX;
- desenvolvimento principal em uma tecnologia mais robusta e mais adequada a performance e escalabilidade;
- manter a versão Python como código histórico e não como base de evolução do projeto.

## Estado atual da arquitetura

A arquitetura atual assume que o Java é a implementação principal do produto:

- a UI é JavaFX;
- os motores de busca e resolução ficam no lado Java;
- o Python é legado e foi abandonado em favor do Java devido a problemas de performance e de manutenção.

## Estrutura funcional

```text
Cube_Solvers/
├── Java/
│   ├── src/main/java/
│   │   ├── cs/*
│   │   └── pt/cubesolvers/*
│   ├── src/test/java/
│   ├── pom.xml
│   └── README.md
├── Python/
│   ├── 2x2/
│   ├── 3x3/
│   ├── NxN/
│   ├── requirements.txt
│   └── README.md
├── docs/
│   ├── ARCHITECTURE.md
│   ├── CONTRIBUTING.md
│   └── API.md
├── README.md
└── .venv/
```

## Camada Java

### 1. `pt.cubesolvers.model`

Responsável pela representação do cubo e pelas operações fundamentais.

Funções principais:

- armazenar os stickers de cada face;
- inicializar cubos resolvidos;
- aplicar movimentos por face, profundidade e número de voltas;
- permitir cópias para evitar mutação acidental de estado;
- serializar o estado interno para integração com solvers.

A classe central é `Cube`.

### 2. `pt.cubesolvers.solver`

Estrutura da ponte entre o modelo de aplicação e os algoritmos de busca.

Cada solver faz o seguinte:

- valida o tamanho do cubo;
- faz uma cópia defensiva do estado;
- converte o cubo para a representação esperada pelo algoritmo da biblioteca subjacente;
- invoca o resolvedor;
- devolve uma solução em forma de lista de movimentos.

Principais classes:

- `Solver2x2`
- `Solver3x3`
- `Solver4x4`
- `Solver5x5`

### 3. `pt.cubesolvers.ui`

Faz a camada de interação com o utilizador.

Responsabilidades:

- carregar a aplicação JavaFX;
- permitir a execução de movimentos e sequências;
- apoiar embaralhamento;
- invocar resolvedores em thread de trabalho independente;
- apresentar alertas de erro e confirmação de solução.

### 4. `cs.*`

Pacotes algorítmicos especialistas.

Cada pacote encapsula uma família de algoritmos específicos:

- `cube222` --- 2×2
- `min2phase` --- 3×3 (Kociemba)
- `threephase` --- 4×4
- `cube555` --- redução e resolução do 5×5

Esses módulos funcionam como motores de busca e não como interface gráfica.

## Camada Python

### 1. `Python/NxN`

É o núcleo do modelo em Python.

A classe `Cubo`:

- mantém cada face em NumPy;
- aplica movimentos em uma ou várias camadas;
- reconhece notações comuns de cubo;
- aceita operações diretas e interações com qualquer módulo externo.

### 2. `Python/2x2`

Implementa a estratégia específica para cubos 2×2.

A lógica principal é:

- normalizar a orientação do cubo;
- procurar solução por BFS bidirecional;
- reverter a normalização para devolver o movimento no referencial original.

### 3. `Python/3x3`

Encapsula o algoritmo de duas fases via `twophase`.

A pipeline é:

- conversão do cubo para facelets URFDLB;
- verificação de consistência do estado;
- invocação do resolvedor;
- devolução da solução em lista de movimentos.

### 4. `Python/NxN/GUI_plan.py`

Interface visual simples em Tkinter para testar o cubo sem necessidade da aplicação Java.

## Fluxos de execução

### Fluxo da interface Java

```text
CubeApplication
  -> CubeApplicationView
  -> Cube
  -> SolverXxX
  -> cs.* search engine
  -> solution list
  -> UI execution
```

### Fluxo do solver Python 3x3

```text
Cubo
  -> Solver3x3._para_facelets()
  -> twophase.solve()
  -> lista de movimentos
```

### Fluxo do solver Python 2x2

```text
Cubo
  -> Solver2x2._orientar_cubo()
  -> BFS bidirecional
  -> _transpor_solucao()
  -> solução no referencial original
```

## Decisões de desenho

### Copiar em vez de mutar internamente

Em vários pontos, os solvers fazem cópias do cubo antes de resolver. Isso evita que uma operação de busca altere o estado visível do utilizador.

### Validação explícita

Cada solver valida:

- dimensão;
- centros das faces;
- contagem de cores;
- possibilidade física do estado.

Isso reduz erros em fases tardias de execução.

### Separação de algoritmos e UI

A lógica de busca fica afastada da interface gráfica. Isso facilita:

- testes automatizados;
- manutenção;
- evolução paralela de algoritmos e UX.

## Conclusão

A arquitetura combina três níveis:

- modelo e representação do cubo;
- motores de resolução especializados;
- interface de execução e visualização.

Essa estrutura permite alavancar as melhores propriedades de cada linguagem e criar uma base de projeto fácil de evoluir para novos tamanhos, novos solvers e novas interfaces.
