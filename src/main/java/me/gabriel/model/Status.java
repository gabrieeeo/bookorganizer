package me.gabriel.model;

public enum Status {

    LIDO("Lido"),
    NAO_LIDO("Não Lido"),
    LENDO("Lendo");

    private String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
