# Módulo Java principal

Este pacote agrega a camada de aplicação e os adaptadores de resolução para os vários tamanhos de cubo.

## Estrutura

```text
pt/cubesolvers/
├── model/
│   └── Cube.java              # estado do cubo, movimentos e serialização
├── solver/
│   ├── Solver2x2.java
│   ├── Solver3x3.java
│   ├── Solver4x4.java
│   ├── Solver5x5.java
│   └── ...
├── ui/
│   ├── CubeApplication.java
│   └── CubeApplicationView.java
├── README.md                 # documentação deste módulo
└── module-info.java          # módulo Java definido pela aplicação
```

## 1. `model`

O pacote `pt.cubesolvers.model` contém a representação do cubo usado pela aplicação JavaFX.

### `Cube`

A classe `Cube`:

- representa o estado interno em `int[][][]`;
- usa as faces `U, D, F, B, L, R`;
- inicializa cubos resolvidos para qualquer tamanho válido;
- implementa movimentos de camada e de múltiplas camadas;
- aceita notação do tipo `R`, `U'`, `Rw`, `3Fw2`;
- fornece cópias e serialização para testes e integração com solvers.

A lógica de movimento é desagregada por face e profundidade, o que permite aplicar sequências complexas sem alterar o estado do cubo original além do que é pedido.

## 2. `solver`

O pacote `solver` aplica os algoritmos externos de resolução dos vários tamanhos.

### `Solver2x2`

- valida cubos `2x2`;
- delega a resolução em `cs.cube222.Search`.

### `Solver3x3`

- valida o tamanho e a consistência da cor dos centros;
- transforma o cubo em facelets compatíveis com Kociemba;
- usa `cs.min2phase.Search` para obter uma solução;
- devolve `Optional.empty()` quando o estado é inválido ou a procura falha pelos critérios do algoritmo.

### `Solver4x4`

- valida cubos `4x4`;
- reduz a configuração para a forma que o solver TPR espera;
- usa `cs.threephase.Search`.

### `Solver5x5`

- valida cubos `5x5`;
- aplica a redução do 5×5 para um estado 3×3 equivalente;
- chama `cs.cube555.Search` e conclui com `cs.min2phase.Search`.

## 3. `ui`

O pacote `ui` implementa a aplicação JavaFX.

### `CubeApplication`

- ponto de entrada da aplicação;
- inicia a janela principal com `Application.launch(...)`.

### `CubeApplicationView`

A janela principal oferece:

- seletor de tamanho do cubo de 2×2 a 11×11;
- sequência manual de movimentos;
- botões por face para executar rotações rápidas;
- botão para embaralhar o cubo;
- botão para calcular uma solução;
- execução da solução na interface.

A aplicação também mantém o estado atual do cubo, valida comandos introduzidos e mostra alertas em caso de erro.

## 4. Módulo Java

O ficheiro `module-info.java` declara o módulo `pt.cubesolvers` e expõe apenas a UI da aplicação:

```java
module pt.cubesolvers {
    requires javafx.controls;
    exports pt.cubesolvers.ui;
}
```

Isto permite separar a API pública visual da lógica interna de modelação e resolução.

## Execução

Na pasta `Java`:

```bash
mvn javafx:run
```

A visualização e a resolução ficam concentradas no módulo principal, enquanto os motores de busca algorítmica vivem nos pacotes `cs.*`.
