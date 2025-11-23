package me.gabriel.model;

import java.io.File;

public record BookModel(String titulo, String autor, int ano, String categoria, File filePath, int totalPaginas) {

}
