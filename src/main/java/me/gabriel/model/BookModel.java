package me.gabriel.model;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class BookModel {
    private Status status = Status.LENDO;
    private String titulo;
    private String autor;
    private int ano;
    private String categoria;
    private String filePath; // ALTERADO de File para String
    private int paginaAtual;
    private int totalPages;

    // Adicionado construtor vazio para o Gson
    public BookModel() {}

    public BookModel(Status status, String titulo, String autor, int ano, String categoria, File file) {
        this.status = status;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.categoria = categoria;
        this.filePath = file.getAbsolutePath(); // Armazena o caminho como String
        this.paginaAtual = 0;
        this.totalPages = 0;
    }

    public Status getStatus() { return status; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }
    public String getCategoria() { return categoria; }
    public int getPaginaAtual() { return paginaAtual; }
    public int getTotalPages() { return totalPages; }

    // O getter agora reconstrói o objeto File a partir do caminho
    public File getFilePath() {
        if (filePath == null) {
            return null;
        }
        return new File(filePath);
    }
    
    public void setPaginaAtual(int paginaAtual) { this.paginaAtual = paginaAtual; }
    public void setStatus(Status status) { this.status = status; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }


    @Override
    public String toString() {
        return titulo + " - " + autor;
    }
}
