# Implementação NxN

Esta pasta contém a representação comum do cubo e a aplicação gráfica:

- `cubo.py`: classe `Cubo`, que guarda cada face numa matriz NumPy e executa
  movimentos de uma ou mais camadas.
- `GUI_plan.py`: interface Tkinter para executar movimentos, gerar scrambles
  e visualizar o cubo.

## Executar a interface

Na raiz do projeto:

```powershell
py NxN\GUI_plan.py
```

Por omissão, é criado um cubo 3x3. Para iniciar outra dimensão, altere
`TAMANHO_N` no bloco final do ficheiro.

## Notação aceite

Os comandos usam as faces `U`, `D`, `F`, `B`, `L` e `R`. O sufixo `'` indica
um quarto de volta anti-horário e `2` uma meia-volta. O sufixo `w` roda as
duas camadas junto à face; um prefixo numérico especifica a profundidade,
como em `3Fw2`. A classe `Cubo` também pode ser importada diretamente por
outros módulos Python.
