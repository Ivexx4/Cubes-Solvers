# Cube Solvers

Este repositório reúne implementações de resolutores de cubo de Rubik em Java e em Python, com foco em visualização, manipulação e solução de estados de cubo para diferentes tamanhos.

## Visão geral

O projeto está dividido em duas grandes áreas:

- Java: aplicação JavaFX para visualizar cubos, aplicar movimentos e resolver estados de 2×2 a 5×5.
- Python: modelos e solvers para cubos 2×2, 3×3 e NxN, com foco em manipulação e pesquisa de solução.

## Estrutura do repositório

```text
Cube_Solvers/
├── Java/                     # Aplicação JavaFX e solvers 2x2–5x5
│   ├── README.md             # Documentação da interface Java
│   ├── pom.xml               # Dependências Maven e configuração do projeto
│   ├── src/                  # Código-fonte Java
│   └── LICENSES/             # Licenças das implementações integradas
├── Python/                   # Solvers e modelo genérico em Python
│   ├── requirements.txt      # Dependências Python
│   ├── 2x2/                 # Solver específico para cubos 2×2
│   ├── 3x3/                 # Solver de duas fases para 3×3
│   ├── NxN/                 # Modelo genérico do cubo e GUI Tkinter
│   └── README.md            # Documentação do conjunto Python (se existir)
├── .venv/                    # Ambiente virtual local
├── .idea/                    # Configuração do IDE
└── README.md                 # Esta documentação principal
```

## Java

A pasta `Java` contém uma aplicação gráfica capaz de:

- representar cubos de vários tamanhos;
- aplicar sequências de movimentos;
- embaralhar o cubo;
- resolver automaticamente estados de 2×2 a 5×5;
- visualizar e testar algoritmos de resolução.

### Requisitos

- JDK 23
- Maven 3.8+

### Execução rápida

```bash
cd Java
mvn javafx:run
```

Para mais detalhes, consulte `Java/README.md`.

## Python

A pasta `Python` contém uma implementação modular para cubos em Python, incluindo:

- modelo genérico `NxN` para aplicar movimentos e representar faces;
- solver `2x2` com procura bidirecional;
- solver `3x3` com algoritmo de duas fases;
- interface Tkinter para manipulação visual do cubo.

### Requisitos

```bash
cd Python
py -m pip install -r requirements.txt
```

### Execução rápida

```bash
cd Python
py 2x2\solver.py
```

ou

```bash
cd Python
py 3x3\solver.py
```

ou ainda

```bash
cd Python
py NxN\GUI_plan.py
```

Para detalhes específicos, consulte os READMEs de cada subpasta:

- `Python/2x2/README.md`
- `Python/3x3/README.md`
- `Python/NxN/README.md`

## Notação de movimentos

Os movimentos usam as faces:

- `U`, `D`, `F`, `B`, `L`, `R`

As convenções comuns são:

- `'` → quarto de volta no sentido inverso
- `2` → meia volta
- `w` → rotações de duas camadas
- prefixo numérico (por exemplo `3Rw`) → profundidade da camada

## Objetivo do projeto

O conjunto foi concebido para demonstrar e testar diferentes abordagens para a resolução de cubos de Rubik, comparando:

- métodos algorítmicos em Python;
- implementações JavaFX para visualização interativa;
- soluções baseadas em busca, normalização e redução de estados.

## Documentação complementar

Cada módulo inclui a sua própria documentação específica:

- `Java/README.md`
- `Java/src/main/java/pt/cubesolvers/README.md`
- `Java/src/main/java/cs/README.md`
- `Python/README.md`
- `Python/2x2/README.md`
- `Python/3x3/README.md`
- `Python/NxN/README.md`

Além disso, os módulos internos foram descritos com foco em:

- objetivo funcional;
- papel de cada pacote/classe principal;
- integração entre a camada de visualização e os motores de resolução;
- limites e requisitos específicos de cada algoritmo.

Se quiser, também posso criar uma documentação adicional em formato de guia de utilização, arquitetura de código ou lista de funcionalidades por módulo.
