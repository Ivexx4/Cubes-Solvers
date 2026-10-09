# Guia de contribuição

Este documento define como participar no projeto de forma consistente e segura.

## Objetivo

O projeto pretende manter uma base estável para:

- estudo de algoritmos de cubo;
- comparação entre implementações em Java e Python;
- visualização e resolução interativa;
- manutenção de testes e documentação técnica.

## Fluxo recomendado

### 1. Preparar o ambiente

#### Java

```bash
cd Java
mvn test
```

#### Python

```powershell
cd Python
py -m pip install -r requirements.txt
```

### 2. Trabalhar sobre um tema específico

Antes de editar:

- identificar o módulo afetado;
- confirmar se a alteração é de API, modelo, solver ou UI;
- manter o alcance o mais pequeno possível;
- preferir alterações em uma única responsabilidade por commit.

### 3. Criar uma branch de trabalho

Exemplo:

```bash
git checkout -b fix/solver-3x3-validation
```

### 4. Testar validações relevantes

Sempre que possível:

- executar os testes unitários afetados;
- testar o fluxo ou comportamento do módulo alterado;
- verificar o impacto sobre a leitura e a manutenção do código.

## Convenções de código

### Java

- usar classes `final` quando a intenção for imutabilidade estrutural;
- manter as entradas públicas com validação explícita;
- evitar lógica gráfica dentro dos motores de solver;
- facilitar a leitura do fluxo de conversão do cubo para o algoritmo.

### Python

- manter nomes claros e expressivos;
- evitar código duplicado em módulos de solução;
- documentar funções importantes em docstrings curtas;
- priorizar legibilidade em comparação com micro-otimizações prematuras.

## Regras de documentação

Toda alteração funcional relevante deve ser acompanhada de:

- documentação do comportamento alterado;
- exemplos de uso, quando aplicável;
- atualização das READMEs do módulo afetado.

## Boas práticas para PRs

- incluir descrição clara do problema e da solução;
- explicar qualquer alteração na interface pública;
- mencionar se a mudança afeta performance, notação ou compatibilidade;
- manter commits pequenos e com mensagem objetiva.

## Testes

### Java

```bash
cd Java
mvn test
```

### Python

Os módulos Python podem ser verificados diretamente com pequenos scripts de execução, por exemplo:

```powershell
cd Python
py 2x2\solver.py
py 3x3\solver.py
```

## Checklist antes de enviar alteração

- [ ] o módulo afetado foi identificado corretamente;
- [ ] a alteração foi testada localmente;
- [ ] a documentação relevante foi atualizada;
- [ ] o código mantém a separação entre modelo, solver e UI;
- [ ] a mensagem final do commit descreve a intenção da mudança.

## Conclusão

Contribuições neste projeto devem ser claras, pequenas e bem documentadas. O foco é manter a lógica de resolução compreensível, fiel ao modelo do cubo e compatível com as ferramentas de visualização e validação existentes.
