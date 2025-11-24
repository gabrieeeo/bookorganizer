package me.gabriel.model;

import java.io.File;

public class BookModel {
    private String titulo;
    private String autor;
    private int ano;
    private String categoria;
    private File filePath;
    private int paginaAtual;

    public BookModel(String titulo, String autor, int ano, String categoria, File filePath) {
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.categoria = categoria;
        this.filePath = filePath;
        this.paginaAtual = 0;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }
    public String getCategoria() { return categoria; }
    public File getFilePath() { return filePath; }
    
    public int getPaginaAtual() { return paginaAtual; }
    public void setPaginaAtual(int paginaAtual) { this.paginaAtual = paginaAtual; }

    @Override
    public String toString() {
        return titulo + " - " + autor;
    }
}
