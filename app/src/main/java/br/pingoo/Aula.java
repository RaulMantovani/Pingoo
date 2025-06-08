package br.pingoo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Aula implements Serializable {
    private int id;
    private String materia;
    private String professor;
    private String diaSemana;
    private List<String> anotacoes;
    private List<Nota> notas;

    public Aula(int id, String materia, String professor, String diaSemana) {
        this.id = id;
        this.materia = materia;
        this.professor = professor;
        this.diaSemana = diaSemana;
        this.anotacoes = new ArrayList<>();
        this.notas = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public List<String> getAnotacoes() { return anotacoes; }

    public List<Nota> getNotas() { return notas; }

    // Método para adicionar uma anotação
    public void adicionarAnotacao(String anotacao) {
        this.anotacoes.add(anotacao);
    }

    // Método para adicionar uma nota com descrição
    public void adicionarNota(Nota nota) {
        this.notas.add(nota);
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    public void setAnotacoes(List<String> anotacoes) {
        this.anotacoes = anotacoes;
    }
}
