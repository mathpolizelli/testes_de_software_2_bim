# Semana 10 — Prática de TDD com Java

Esta pasta reúne quatro projetos independentes para praticar desenvolvimento orientado a testes (TDD) com Java 17, Maven e JUnit 5. Em cada projeto, implemente os comportamentos gradualmente seguindo o ciclo **RED → GREEN → REFACTOR**: escreva um teste que falha, implemente o necessário para fazê-lo passar e refatore mantendo os testes verdes.

## Projetos

| Projeto | Foco | Orientações |
| --- | --- | --- |
| [`bubblesort-tdd`](bubblesort-tdd/) | Implementação do algoritmo Bubble Sort a partir dos testes. | Consulte [`REQUISITOS.md`](bubblesort-tdd/REQUISITOS.md). A implementação está propositalmente omitida. |
| [`carrinho-compras-tdd`](carrinho-compras-tdd/) | Construção de um carrinho de compras guiada por requisitos e testes. | Consulte [`REQUISITOS.md`](carrinho-compras-tdd/REQUISITOS.md) e crie os testes antes da implementação. |
| [`conta-bancaria-tdd-maven`](conta-bancaria-tdd-maven/) | Desenvolvimento incremental de uma conta bancária. | A classe `ContaBancaria` está propositalmente incompleta; use o roteiro no README do projeto. |
| [`estoque-tdd`](estoque-tdd/) | Exemplo de TDD para operações de estoque de produtos. | Os cenários incluem entradas, retiradas e validações de quantidades. Consulte o README do projeto. |

## Pré-requisitos

- JDK 17 ou superior
- Apache Maven instalado e disponível no `PATH`

## Executar um projeto

Cada subpasta é um projeto Maven separado. Entre na pasta desejada e execute os testes:

```bash
cd bubblesort-tdd
mvn test
```

Substitua `bubblesort-tdd` por `carrinho-compras-tdd`, `conta-bancaria-tdd-maven` ou `estoque-tdd` para executar os testes de outro exercício. Os testes devem ser executados a partir da pasta que contém o respectivo `pom.xml`.

## Fluxo sugerido

1. Leia o `README.md` e, quando disponível, o arquivo `REQUISITOS.md` do projeto escolhido.
2. Execute `mvn test` para observar o estado inicial.
3. Trabalhe em um requisito por vez: teste primeiro, implementação depois.
4. Refatore quando os testes estiverem passando e execute `mvn test` novamente.

Os exercícios têm objetivos e níveis de implementação diferentes; consulte as instruções de cada projeto antes de começar.
