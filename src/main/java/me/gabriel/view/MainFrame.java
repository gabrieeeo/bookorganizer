package me.gabriel.view;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import me.gabriel.controller.LibraryController;
import me.gabriel.model.BookModel;
import me.gabriel.model.Status;

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
        String[] columnNames = {"Status", "Título", "Autor", "Ano", "Categoria", "Página", "Total de Páginas"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Apenas a coluna Status é editável
            }
        };
        bookTable = new JTable(tableModel);
        
        // Configura o ComboBox para a coluna Status
        JComboBox<Status> statusComboBox = new JComboBox<>(Status.values());
        TableColumn statusColumn = bookTable.getColumnModel().getColumn(0);
        statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

        // Listener para atualizar o modelo quando a tabela for editada
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0 && controller != null) {
                    Status newStatus = (Status) tableModel.getValueAt(row, column);
                    controller.updateBookStatus(row, newStatus);
                }
            }
        });

        add(new JScrollPane(bookTable), BorderLayout.CENTER);
    }

    public void addBookToView(BookModel book) {
        Object[] rowData = {book.getStatus(), book.getTitulo(), book.getAutor(), book.getAno(), book.getCategoria(), book.getPaginaAtual(), book.getTotalPages()};
        tableModel.addRow(rowData);
    }

}
