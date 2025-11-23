package me.gabriel.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.gabriel.model.BookModel;
import me.gabriel.view.BookFormDialog;
import me.gabriel.view.MainFrame;
import me.gabriel.view.PdfViewer;

public class LibraryController {

    private final MainFrame view;
    private final List<BookModel> books = new ArrayList<>();

    public LibraryController(MainFrame view) {
        this.view = view;
        this.view.setController(this); // Conecta a View ao Controller
    }

    public void importBook() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione um Ebook PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF", "pdf"));

        int userSelection = fileChooser.showOpenDialog(view);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Abre o modal para preencher os dados
            BookFormDialog dialog = new BookFormDialog(view);
            dialog.setVisible(true);

            if (dialog.isSubmitted()) {
                BookModel newBook = new BookModel(
                    dialog.getTitulo(), 
                    dialog.getAutor(), 
                    dialog.getAno(), 
                    dialog.getCategoria(), 
                    fileToSave, 
                    0 // Total de páginas (placeholder)
                );
                
                books.add(newBook);
                System.out.println("Livro importado: " + newBook);
                
                // Atualiza a tela
                view.addBookToView(newBook);
            }
        }
    }

    public void openBook(int index) {
        if (index >= 0 && index < books.size()) {
            BookModel book =  books.get(index);
            File file = book.filePath();
            
            if (file.exists()) {
                // Abre o visualizador interno
                PdfViewer viewer = new PdfViewer(file);
                viewer.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view, "Arquivo não encontrado: " + file.getAbsolutePath());
            }
        }
    }
}
