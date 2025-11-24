package me.gabriel.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import me.gabriel.controller.LibraryController;
import me.gabriel.model.BookModel;

import java.awt.BorderLayout;

public class MainFrame extends JFrame{

    private LibraryController controller;
    private DefaultTableModel tableModel;
    private JTable bookTable;

    public MainFrame() {
        setTitle("Book Organizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        iniciarUI();
    }

    public MainFrame(LibraryController controller) {
        this.controller = controller;
    }

    public void setController(LibraryController controller) {
        this.controller = controller;
    }

    public void iniciarUI() {
        // Layout principal
        setLayout(new BorderLayout());

        // === Barra de Ferramentas ===
        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Adicionar PDF");
        JButton btnRead = new JButton("Ler Selecionado");
        JButton btnDashboardButton = new JButton("Dashboard");
        
        // Ações dos botões (chamando o Controller)
        btnAdd.addActionListener(e -> {
            if (controller != null) controller.importBook();
        });
        
        btnRead.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                if (controller != null) controller.openBook(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para ler.");
            }
        });

        toolBar.add(btnAdd);
        toolBar.add(btnRead);
        toolBar.add(btnDashboardButton);
        add(toolBar, BorderLayout.NORTH);

        // === Área Central (Lista de Livros) ===
        String[] columnNames = {"Título", "Autor", "Ano", "Categoria"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
    }

    public void addBookToView(BookModel book) {
        Object[] rowData = {book.getTitulo(), book.getAutor(), book.getAno(), book.getCategoria()};
        tableModel.addRow(rowData);
    }

}
