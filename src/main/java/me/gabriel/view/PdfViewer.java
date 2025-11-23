package me.gabriel.view;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import java.io.File;

public class PdfViewer extends JFrame {

    public PdfViewer(File pdfFile) {
        setTitle("Leitor: " + pdfFile.getName());
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Build a controller
        SwingController controller = new SwingController();

        // Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder(controller);

        // Use the factory to build a JPanel that is pre-configured
        // with a complete, active Viewer UI.
        JPanel viewerComponentPanel = factory.buildViewerPanel();

        // Add the viewer component to this JFrame
        getContentPane().add(viewerComponentPanel);

        // Open a PDF document to view
        controller.openDocument(pdfFile.getAbsolutePath());
    }
}
