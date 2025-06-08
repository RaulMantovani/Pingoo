package br.pingoo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Classe que representa um evento
public class Evento {
    private String nomeEvento;
    private LocalDate dataEvento;

    public Evento(String nomeEvento, LocalDate dataEvento) {
        this.nomeEvento = nomeEvento;
        this.dataEvento = dataEvento;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    @Override
    public String toString() {
        return nomeEvento + " - " + dataEvento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
