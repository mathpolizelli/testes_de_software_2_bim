# Semana 11 — Testes de integração com Spring Boot e JUnit 5

O enunciado original da atividade está em [ENUNCIADO.md](ENUNCIADO.md).

**Tecnologias:** Java 17+, Maven, Spring Boot 3.3, Spring Data JPA, H2 (em memória), JUnit 5 e MockMvc.

## Status

| Exercício | Caso | Teste | Status |
|---|---|---|---|
| 1 | CT01 — Cadastro válido | `deveCadastrarERecuperarProduto` | ✅ Feito |
| 1 | CT02 — Nome inválido | `naoDevePersistirProdutoComNomeEmBranco` | ✅ Feito |
| 1 | CT03 — Atualização (desafio) | `deveAtualizarPrecoPersistido` | ✅ Feito |
| 2 | CT04 — Cadastro via API | `deveCadastrarProdutoPelaApi` | ✅ Feito |
| 2 | CT05 — Consulta via API | `deveBuscarProdutoPelaApi` | ✅ Feito |

Nenhum mock é usado em nenhum dos exercícios: `ProdutoService` e `ProdutoRepository` são os beans
reais, e os dados são gravados no banco H2 em memória.

## Exercício 1 — Service + Repository + H2

Arquivo: [`ProdutoServiceIntegrationTest.java`](src/test/java/br/edu/exemplo/ProdutoServiceIntegrationTest.java)

O teste sobe o contexto Spring completo (`@SpringBootTest`) e injeta o `ProdutoService` e o
`ProdutoRepository` reais (`@Autowired`).

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

Arquivo: [`ProdutoControllerIntegrationTest.java`](src/test/java/br/edu/exemplo/ProdutoControllerIntegrationTest.java)

A classe usa `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional`, e as requisições são
feitas com `mockMvc.perform(...)`.

- **CT04 — Cadastro via API:** `POST /produtos` com `{"nome":"Mouse","preco":80.0}` e
  `contentType(MediaType.APPLICATION_JSON)`. Verifica `status().isOk()` (HTTP 200) e, com `jsonPath`,
  que `$.id` é numérico, `$.nome` é `Mouse` e `$.preco` é `80.0`.
- **CT05 — Consulta via API:** faz um `POST /produtos` cadastrando `Teclado` por `150.0`, captura o
  corpo da resposta com `andReturn().getResponse().getContentAsString()` e extrai o ID com
  `ObjectMapper.readTree(...)` (`JsonNode`). Depois executa `GET /produtos/{id}` e confere HTTP 200
  e que `id`, `nome` e `preco` do JSON batem com o produto cadastrado.

Para garantir que os testes realmente verificam algo, os valores esperados foram trocados
temporariamente (nome `Errado`, preço `81.0`) e os dois testes falharam como esperado
(`JSON path "$.nome" expected:<Errado> but was:<Teclado>`); depois os valores corretos foram restaurados.

### Discussão

**Quais componentes são efetivamente exercitados?**
O fluxo completo da aplicação: o `DispatcherServlet` do Spring MVC (roteamento da URL para o método),
a desserialização do JSON da requisição para `ProdutoRequest` e a serialização do `Produto` de volta
para JSON (Jackson), o `ProdutoController`, as regras do `ProdutoService`, o `ProdutoRepository`
(Spring Data JPA / Hibernate) e o banco H2. Só o servidor HTTP e a rede ficam de fora.

**Por que o MockMvc não precisa iniciar um servidor HTTP real?**
O MockMvc cria objetos de requisição e resposta simulados (`MockHttpServletRequest` /
`MockHttpServletResponse`) e os entrega diretamente ao `DispatcherServlet` dentro da mesma JVM.
Como `@SpringBootTest` usa por padrão um ambiente web "mock" (`WebEnvironment.MOCK`), nenhum Tomcat é
iniciado e nenhuma porta é aberta — o teste fica mais rápido e sem conflito de portas, mas toda a
camada web do Spring é executada normalmente.

**Qual é a diferença entre esse teste e um teste unitário do controller com dependências simuladas?**
Num teste unitário (por exemplo, `@WebMvcTest` com o `ProdutoService` substituído por um mock), só o
controller é testado: o retorno do serviço é inventado pelo próprio teste, então ele não detecta
erros na regra de negócio, no mapeamento JPA ou na persistência. Ele é mais rápido e isola falhas no
controller. Já o teste de integração usa os componentes reais e o banco, então prova que as camadas
funcionam **juntas** (o produto é realmente salvo e depois recuperado pelo GET), ao custo de ser mais
lento e de uma falha poder vir de qualquer camada.

### Isolamento entre os testes

As duas classes de teste são anotadas com `@Transactional`. Inclusive no Exercício 2 isso funciona,
porque o MockMvc executa a requisição **na mesma thread** do teste: o controller, o serviço e o
repositório participam da transação aberta pelo teste, que sofre rollback ao final. Nenhum produto
cadastrado em um teste fica visível para outro. O banco H2 é em memória
(`jdbc:h2:mem:produtos`) e o schema é criado/destruído pelo Hibernate (`ddl-auto=create-drop`).

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
