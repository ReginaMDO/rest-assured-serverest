package Entities;

import java.util.List;

public class Carts {

    private List<CartItem> produtos;

    public Carts(List<CartItem> produtos) {
        this.produtos = produtos;
    }

    public List<CartItem> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<CartItem> produtos) {
        this.produtos = produtos;
    }

    public static class CartItem {
        private String idProduto;
        private int quantidade;

        public CartItem(String idProduto, int quantidade) {
            this.idProduto = idProduto;
            this.quantidade = quantidade;
        }

        public String getIdProduto() {
            return idProduto;
        }

        public void setIdProduto(String idProduto) {
            this.idProduto = idProduto;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }
}

