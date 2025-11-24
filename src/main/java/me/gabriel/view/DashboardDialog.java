package me.gabriel.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;
import me.gabriel.model.Status;

public class DashboardDialog extends JDialog {

    public DashboardDialog(Frame owner, Map<Status, Long> bookStats) {
        super(owner, "Dashboard de Leitura", true);
        setSize(400, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Extrai os valores do mapa, tratando casos onde não há livros com um certo status
        long lidosCount = bookStats.getOrDefault(Status.LIDO, 0L);
        long lendoCount = bookStats.getOrDefault(Status.LENDO, 0L);
        long naoLidoCount = bookStats.getOrDefault(Status.NAO_LIDO, 0L);
        long total = lidosCount + lendoCount + naoLidoCount;

        mainPanel.add(createStatPanel("Livros Lidos", Long.toString(lidosCount), new Color(76, 175, 80)));
        mainPanel.add(createStatPanel("Lendo Atualmente", Long.toString(lendoCount), new Color(3, 169, 244)));
        mainPanel.add(createStatPanel("Não Lidos", Long.toString(naoLidoCount), new Color(244, 67, 54)));
        
        JLabel totalLabel = new JLabel("Total de Livros na Biblioteca: " + total, SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        add(mainPanel, BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }

    private JPanel createStatPanel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(color);
        
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
}
