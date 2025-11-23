package me.gabriel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import me.gabriel.controller.LibraryController;
import me.gabriel.view.MainFrame;

public class Main {

    public static void main(String[] args) {

      //Configurar o FlatLaf como tema padrão da aplicação antes de iniciar a interface gráfica.
      try {
        UIManager.setLookAndFeel(new FlatDarkLaf());
      } catch (Exception e) {
        System.err.println("Failed to initialize FlatLaf:" + e);
      }  

      SwingUtilities.invokeLater(() -> {
        MainFrame mainFrame = new MainFrame();
        new LibraryController(mainFrame);
        mainFrame.setVisible(true);
      });
    }
}
