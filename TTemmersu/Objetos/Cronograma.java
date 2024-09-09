package com.example.tasktide.Objetos;

public class Cronograma {
    private String horario;
    private String nomeAtividade;

    public Cronograma(String horario, String nomeAtividade) {
        this.horario = horario;
        this.nomeAtividade = nomeAtividade;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }
}
