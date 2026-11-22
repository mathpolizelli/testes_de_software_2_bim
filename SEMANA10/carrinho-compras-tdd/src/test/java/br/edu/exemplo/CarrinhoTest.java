package br.edu.exemplo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarrinhoTest {

    private static final double DELTA = 0.001;

    // Requisito 1
    @Test
    void carrinhoVazioDeveTerTotalZero() {
        Carrinho carrinho = new Carrinho();

        assertEquals(0, carrinho.getTotal(), DELTA);
    }

    // Requisito 2
    @Test
    void deveAdicionarUmProduto() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Teclado", 100));

        assertEquals(100, carrinho.getTotal(), DELTA);
    }

    // Requisito 3
    @Test
    void deveSomarVariosProdutos() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Teclado", 100));
        carrinho.adicionar(new Produto("Mouse", 50));

        assertEquals(150, carrinho.getTotal(), DELTA);
    }

    // Requisito 4
    @Test
    void deveRemoverProduto() {
        Carrinho carrinho = new Carrinho();
        Produto teclado = new Produto("Teclado", 100);
        Produto mouse = new Produto("Mouse", 50);
        carrinho.adicionar(teclado);
        carrinho.adicionar(mouse);

        carrinho.remover(mouse);

        assertEquals(100, carrinho.getTotal(), DELTA);
    }

    // Requisito 5
    @Test
    void naoDevePermitirProdutoComPrecoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Teclado", -100));
    }

    // Requisito 6
    @Test
    void deveAplicarDescontoDe10PorCentoAcimaDe500() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Monitor", 600));

        assertEquals(600, carrinho.getSubtotal(), DELTA);
        assertEquals(60, carrinho.getDesconto(), DELTA);
        assertEquals(540, carrinho.getTotal(), DELTA);
    }

    @Test
    void naoDeveAplicarDescontoAbaixoDe500() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Produto", 499.99));

        assertEquals(499.99, carrinho.getTotal(), DELTA);
    }

    @Test
    void deveAplicarDescontoExatamenteEm500() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Produto", 500.00));

        assertEquals(450.00, carrinho.getTotal(), DELTA);
    }

    @Test
    void deveAplicarDescontoLogoAcimaDe500() {
        Carrinho carrinho = new Carrinho();

        carrinho.adicionar(new Produto("Produto", 500.01));

        assertEquals(450.009, carrinho.getTotal(), DELTA);
    }
}
