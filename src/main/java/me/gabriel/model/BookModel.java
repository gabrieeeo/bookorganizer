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
    private File filePath;
    private int paginaAtual;

    public BookModel(Status status, String titulo, String autor, int ano, String categoria, File filePath) {
        this.status = status;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.categoria = categoria;
        this.filePath = filePath;
        this.paginaAtual = 0;
    }

    public Status getStatus() { return status; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAno() { return ano; }
    public String getCategoria() { return categoria; }
    public File getFilePath() { return filePath; }
    public int getPaginaAtual() { return paginaAtual; }

    public int getTotalPages() {
        try (PDDocument document = Loader.loadPDF(filePath)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo PDF: " + e.getMessage());
            return -1; // Retorna -1 para indicar um erro
        }
    }

    public void setPaginaAtual(int paginaAtual) { this.paginaAtual = paginaAtual; }
    public void setStatus(Status status) { this.status = status; }

    

    @Override
    public String toString() {
        return titulo + " - " + autor;
    }
}
