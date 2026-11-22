# Semana 11 — Testes de integração com Spring Boot e JUnit 5

> ⚠️ **ENTREGA INCOMPLETA** — apenas o **Exercício 1** (CT01, CT02 e CT03) foi resolvido.
> O **Exercício 2** (CT04 e CT05 — `ProdutoControllerIntegrationTest`) **ainda não foi feito**:
> os métodos continuam com `TODO` e, por estarem vazios, aparecem como "passando" no `mvn test`,
> mas não verificam nada.

O enunciado original da atividade está em [ENUNCIADO.md](ENUNCIADO.md).

**Tecnologias:** Java 17+, Maven, Spring Boot 3.3, Spring Data JPA, H2 (em memória), JUnit 5.

## Status

| Exercício | Caso | Teste | Status |
|---|---|---|---|
| 1 | CT01 — Cadastro válido | `deveCadastrarERecuperarProduto` | ✅ Feito |
| 1 | CT02 — Nome inválido | `naoDevePersistirProdutoComNomeEmBranco` | ✅ Feito |
| 1 | CT03 — Atualização (desafio) | `deveAtualizarPrecoPersistido` | ✅ Feito |
| 2 | CT04 — Cadastro via API | `deveCadastrarProdutoPelaApi` | ❌ Pendente |
| 2 | CT05 — Consulta via API | `deveBuscarProdutoPelaApi` | ❌ Pendente |

## Exercício 1 — Service + Repository + H2

Arquivo: [`ProdutoServiceIntegrationTest.java`](src/test/java/br/edu/exemplo/ProdutoServiceIntegrationTest.java)

Nenhum mock é usado: o teste sobe o contexto Spring completo (`@SpringBootTest`), injeta o
`ProdutoService` e o `ProdutoRepository` reais (`@Autowired`) e grava no banco H2 em memória.

- **CT01 — Cadastro válido:** cadastra `Teclado` por `150.0` pelo serviço, verifica com
  `assertNotNull` que o produto recebeu ID e então busca pelo `repository.findById()`,
  conferindo nome e preço com `assertEquals`.
- **CT02 — Nome inválido:** guarda `repository.count()`, tenta cadastrar com nome em branco e
  preço `80.0`, verifica `IllegalArgumentException` com `assertThrows` e confirma que a contagem
  não mudou.
- **CT03 — Atualização:** cadastra `Mouse` por `80.0`, chama `atualizarPreco(id, 95.0)` e busca
  novamente pelo repositório, verificando preço `95.0`.

Nos testes CT01 e CT03, antes da consulta é feito `entityManager.flush()` + `entityManager.clear()`.
Sem isso, como tudo roda na mesma transação, o `findById()` devolveria o mesmo objeto que já está
no cache do JPA (contexto de persistência) sem nem consultar o banco. Com o `flush` o `INSERT`/`UPDATE`
é enviado ao H2, e com o `clear` o cache é esvaziado — assim a busca realmente faz um `SELECT`.

### Discussão

**Por que consultar com `repository.findById()` é uma evidência diferente de olhar só o objeto
retornado pelo serviço?**
O objeto retornado pelo serviço só prova que o método devolveu algo com os valores certos — ele
poderia, por exemplo, devolver o próprio objeto montado em memória sem ter salvo nada. Buscar de novo
pelo repositório (ainda mais depois de limpar o contexto de persistência) prova que o dado de fato
foi persistido e pode ser lido de volta do banco, ou seja, que service, repository, mapeamento JPA e
H2 estão integrados corretamente.

**O que o `@Transactional` faz ao término de cada teste?**
Em classes de teste, o Spring abre uma transação antes de cada método e faz **rollback** ao final.
Assim, os produtos cadastrados em um teste não ficam no banco para o próximo: cada teste começa com o
banco no mesmo estado, o que garante o isolamento e evita que a ordem de execução influencie o
resultado (por exemplo, a contagem do CT02).

## Exercício 2 — Integração HTTP com MockMvc

> ❌ **Ainda não implementado.**

## Execução

```bash
mvn test
```

O projeto exige Java 17 ou superior (Spring Boot 3). Se o `java` padrão da máquina for mais antigo,
basta apontar o `JAVA_HOME` para um JDK 17+ antes de rodar o Maven.

### Resultado obtido

```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in br.edu.exemplo.ProdutoServiceIntegrationTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in br.edu.exemplo.ProdutoControllerIntegrationTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Observação: os 2 testes de `ProdutoControllerIntegrationTest` passam apenas porque ainda estão vazios
(Exercício 2 pendente). Os 3 testes do Exercício 1 contêm asserções reais e passam.
