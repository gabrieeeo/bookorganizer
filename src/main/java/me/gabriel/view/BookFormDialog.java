package me.gabriel.view;

import javax.swing.*;

import me.gabriel.model.Status;

import java.awt.*;

public class BookFormDialog extends JDialog {
    private JTextField titleField;
    private JTextField authorField;
    private JTextField yearField;
    private JTextField categoryField;
    private boolean submitted = false;

    public BookFormDialog(Frame parent) {
        super(parent, "Detalhes do Livro", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        add(new JLabel("Título:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Autor:"));
        authorField = new JTextField();
        add(authorField);

        add(new JLabel("Ano:"));
        yearField = new JTextField();
        add(yearField);

        add(new JLabel("Categoria:"));
        categoryField = new JTextField();
        add(categoryField);

        JButton btnOk = new JButton("Salvar");
        btnOk.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() || 
                authorField.getText().trim().isEmpty() || 
                categoryField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Por favor, preencha todos os campos obrigatórios.", 
                    "Campos Obrigatórios", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "O campo Ano deve ser um número válido.", 
                    "Erro de Formato", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            submitted = true;
            dispose();
        });
        add(btnOk);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Status getStatus() {
        return Status.LENDO; // Default status for new books
    }

    public String getTitulo() {
        return titleField.getText();
    }

    public String getAutor() {
        return authorField.getText();
    }

    public int getAno() {
        try {
            return Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            return 0; // Valor padrão se não for número
        }
    }

    public String getCategoria() {
        return categoryField.getText();
    }
}
