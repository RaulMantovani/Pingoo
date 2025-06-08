package br.pingoo;

import java.io.Serializable;

public class Nota implements Serializable {
    private float valor;
    private String descricao;

    // Construtor
    public Nota(float valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao + " (" + valor + ")";
    }
}
