# Módulos Python

Esta pasta reúne os módulos responsáveis pela representação, manipulação e resolução de cubos em Python.

## Estrutura

```text
Python/
├── 2x2/
│   ├── solver.py             # resolvedor específico para cubos 2×2
│   └── README.md            # documentação do módulo 2×2
├── 3x3/
│   ├── solver.py             # wrapper sobre o algoritmo de duas fases
│   └── README.md            # documentação do módulo 3×3
├── NxN/
│   ├── cubo.py              # modelo genérico para cubos NxN
│   ├── GUI_plan.py          # interface Tkinter para visualização e movimentos
│   └── README.md            # documentação do modelo e GUI
├── requirements.txt          # dependências do ambiente Python
└── README.md                # documentação deste conjunto de módulos
```

## 1. Módulo NxN

O núcleo do projeto em Python está em `NxN/cubo.py`.

### Responsabilidades

- criar cubos de dimensão `N` no estado resolvido;
- manter cada face como uma matriz NumPy;
- aplicar movimentos simples (`R`, `U'`, `F2`) e movimentos wide (`Rw`, `3Fw2`);
- validar estados por inspeção de faces;
- suportar integração com solvers externos e internos.

### Regras de notação suportadas

- `U`, `D`, `F`, `B`, `L`, `R`
- `'` = quarto de volta anti-horário
- `2` = meia volta
- `w` = duas camadas
- prefixo numérico = profundidade da camada, por exemplo `3Rw`

A classe `Cubo` pode ser importada diretamente por outros módulos e usada como base para algoritmos de solução.

## 2. Módulo 2×2

O módulo `2x2/solver.py` implementa a classe `Solver2x2`.

### Estratégia

- valida a dimensão (`N == 2`);
- normaliza a orientação do cubo para evitar redundâncias de estado;
- usa pesquisa bidirecional em largura em `U`, `R` e `F`;
- converte a solução do referencial normalizado para o referencial original.

### Quando usar

- quando se deseja resolver cubos 2×2 de forma robusta e rápida;
- quando se necessita de uma solução relativamente curta sem depender de bibliotecas externas.

## 3. Módulo 3×3

O módulo `3x3/solver.py` implementa `Solver3x3`.

### Estratégia

- converte o cubo para a representação de facelets URFDLB;
- valida se a configuração é fisicamente possível;
- chama o algoritmo de duas fases de Kociemba via `twophase`.

### Limites configuráveis

- `max_length`: comprimento máximo da solução;
- `timeout`: limite de tempo de pesquisa.

### Observações

A primeira execução pode demorar porque a biblioteca gera tabelas de pesquisa em cache. As execuções seguintes tendem a ser muito mais rápidas.

## 4. Interface gráfica

`NxN/GUI_plan.py` oferece uma interface Tkinter para:

- visualizar o cubo em 3D/2D simplificado;
- aplicar movimentos manuais;
- gerar scrambles;
- testar estados e verificar a resolução.

## Execução rápida

No diretório raiz do projeto:

```powershell
py -m pip install -r requirements.txt
py 2x2\solver.py
py 3x3\solver.py
py NxN\GUI_plan.py
```

## Nota

Os módulos Python foram desenhados para serem simples, explícitos e fáceis de reutilizar em testes automáticos, prototipagem e ensino de algoritmos de resolução de cubos.
