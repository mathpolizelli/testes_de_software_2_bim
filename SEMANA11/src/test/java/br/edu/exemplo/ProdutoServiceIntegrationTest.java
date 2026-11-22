package br.edu.exemplo;
import br.edu.exemplo.model.Produto;
import br.edu.exemplo.repository.ProdutoRepository;
import br.edu.exemplo.service.ProdutoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @Transactional
class ProdutoServiceIntegrationTest {
 @Autowired ProdutoService service;
 @Autowired ProdutoRepository repository;
 @Autowired EntityManager entityManager;

 // CT01 — Cadastro válido
 @Test void deveCadastrarERecuperarProduto(){
  Produto salvo = service.cadastrar("Teclado", 150.0);

  assertNotNull(salvo.getId(), "O produto deve receber um ID ao ser persistido");

  // Força a escrita no H2 e limpa o cache do JPA, garantindo que a busca abaixo vá ao banco
  entityManager.flush();
  entityManager.clear();

  Produto encontrado = repository.findById(salvo.getId()).orElse(null);
  assertNotNull(encontrado, "O produto deve ser encontrado no repositório");
  assertEquals("Teclado", encontrado.getNome());
  assertEquals(150.0, encontrado.getPreco(), 0.001);
 }

 // CT02 — Nome inválido
 @Test void naoDevePersistirProdutoComNomeEmBranco(){
  long quantidadeAntes = repository.count();

  assertThrows(IllegalArgumentException.class, () -> service.cadastrar("   ", 80.0));

  assertEquals(quantidadeAntes, repository.count(), "Nenhum registro deve ser criado");
 }

 // CT03 — Atualização (desafio)
 @Test void deveAtualizarPrecoPersistido(){
  Produto mouse = service.cadastrar("Mouse", 80.0);
  Long id = mouse.getId();

  service.atualizarPreco(id, 95.0);

  entityManager.flush();
  entityManager.clear();

  Produto atualizado = repository.findById(id).orElseThrow();
  assertEquals("Mouse", atualizado.getNome());
  assertEquals(95.0, atualizado.getPreco(), 0.001);
 }
}
