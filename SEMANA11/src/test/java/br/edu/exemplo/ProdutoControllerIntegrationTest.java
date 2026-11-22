package br.edu.exemplo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
@SpringBootTest @AutoConfigureMockMvc @Transactional
class ProdutoControllerIntegrationTest {
 @Autowired MockMvc mockMvc;
 @Autowired ObjectMapper objectMapper;

 // CT04 — Cadastro via API
 @Test void deveCadastrarProdutoPelaApi() throws Exception {
  mockMvc.perform(post("/produtos")
     .contentType(MediaType.APPLICATION_JSON)
     .content("{\"nome\":\"Mouse\",\"preco\":80.0}"))
   .andExpect(status().isOk())
   .andExpect(jsonPath("$.id").isNumber())
   .andExpect(jsonPath("$.nome").value("Mouse"))
   .andExpect(jsonPath("$.preco").value(80.0));
 }

 // CT05 — Consulta via API
 @Test void deveBuscarProdutoPelaApi() throws Exception {
  String respostaPost = mockMvc.perform(post("/produtos")
     .contentType(MediaType.APPLICATION_JSON)
     .content("{\"nome\":\"Teclado\",\"preco\":150.0}"))
   .andExpect(status().isOk())
   .andReturn().getResponse().getContentAsString();

  JsonNode json = objectMapper.readTree(respostaPost);
  long id = json.get("id").asLong();

  mockMvc.perform(get("/produtos/{id}", id))
   .andExpect(status().isOk())
   .andExpect(jsonPath("$.id").value(id))
   .andExpect(jsonPath("$.nome").value("Teclado"))
   .andExpect(jsonPath("$.preco").value(150.0));
 }
}
