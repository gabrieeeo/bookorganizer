package me.gabriel.view;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfViewer extends JFrame {

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private JLabel pageLabel;
    private JLabel statusLabel;
    
    // Minimap
    private JList<Integer> thumbnailList;
    private DefaultListModel<Integer> listModel;
    private final java.util.Map<Integer, ImageIcon> thumbnailCache = new java.util.concurrent.ConcurrentHashMap<>();
    
    // Controle de thread
    private volatile boolean isClosed = false;

    // Controles de visualização
    private float zoomFactor = 1.5f;

    public PdfViewer(File pdfFile, int startPage) {
        setTitle("Leitor: " + pdfFile.getName());
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            document = Loader.loadPDF(pdfFile);

            // Custom Renderer para forçar anti-aliasing máximo na fonte
            renderer = new PDFRenderer(document) {
                @Override
                protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
                    return new PageDrawer(parameters) {
                        public void drawPage(Graphics2D g, PDRectangle pageSize) throws IOException {
                            super.drawPage(g, pageSize);
                        }
                    };
                }
            };

            // Valida a página inicial
            if (startPage >= 0 && startPage < document.getNumberOfPages()) {
                this.currentPage = startPage;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar PDF: " + e.getMessage());
            dispose();
            return;
        }

        initUI();
        
        // Carrega thumbnails em background
        new Thread(this::loadThumbnails).start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isClosed = true; // Sinaliza que a janela está fechando
                try {
                    if (document != null)
                        document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void loadThumbnails() {
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            if (isClosed) return; // Interrompe se a janela foi fechada
            
            try {
                // Renderiza thumbnail pequena (largura ~100px)
                BufferedImage thumb = renderer.renderImageWithDPI(i, 30); // 30 DPI é suficiente para thumbnail
                int width = 115;
                int height = (int) (thumb.getHeight() * ((double) width / thumb.getWidth()));
                Image scaled = thumb.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                thumbnailCache.put(i, new ImageIcon(scaled));
                
                // Atualiza a lista na UI thread a cada 5 páginas para não travar
                if (i % 5 == 0 && !isClosed) SwingUtilities.invokeLater(() -> thumbnailList.repaint());
                
            } catch (Exception e) {
                // Se o erro for porque o documento fechou, apenas ignora
                if (!isClosed) e.printStackTrace();
            }
        }
        if (!isClosed) SwingUtilities.invokeLater(() -> thumbnailList.repaint());
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnPrev = new JButton("< Anterior");
        JButton btnNext = new JButton("Próximo >");

        statusLabel = new JLabel(" Página 1 ");

        // Ações
        btnPrev.addActionListener(e -> {
            showPage(currentPage - 1);
        });

        btnNext.addActionListener(e -> {
            showPage(currentPage + 1);
        });

        toolBar.add(btnPrev);
        toolBar.add(statusLabel);
        toolBar.add(btnNext);

        add(toolBar, BorderLayout.NORTH);

        // Page View
        pageLabel = new JLabel();
        pageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(pageLabel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // === MINIMAP (Thumbnail Sidebar) ===
        listModel = new DefaultListModel<>();
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            listModel.addElement(i);
        }
        
        thumbnailList = new JList<>(listModel);
        thumbnailList.setCellRenderer(new ThumbnailRenderer());
        thumbnailList.setBackground(Color.DARK_GRAY);
        thumbnailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thumbnailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = thumbnailList.getSelectedIndex();
                if (selected != -1 && selected != currentPage) {
                    showPage(selected);
                }
            }
        });
        
        JScrollPane thumbnailScroll = new JScrollPane(thumbnailList);
        thumbnailScroll.setPreferredSize(new Dimension(160, 0));
        thumbnailScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(thumbnailScroll, BorderLayout.EAST);

        showPage(currentPage);
    }
    
    private class ThumbnailRenderer extends JPanel implements ListCellRenderer<Integer> {
        private final JLabel imageLabel = new JLabel();
        private final JLabel numberLabel = new JLabel();

        public ThumbnailRenderer() {
            setLayout(new BorderLayout());
            setBackground(Color.DARK_GRAY);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
            numberLabel.setForeground(Color.WHITE);
            
            add(imageLabel, BorderLayout.CENTER);
            add(numberLabel, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Integer> list, Integer pageIndex, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            numberLabel.setText("Pág " + (pageIndex + 1));
            
            if (isSelected) {
                setBackground(new Color(120, 120, 120));
            } else {
                setBackground(Color.DARK_GRAY);
            }
            
            ImageIcon icon = thumbnailCache.get(pageIndex);
            if (icon != null) {
                imageLabel.setIcon(icon);
                imageLabel.setText("");
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("...");
                imageLabel.setForeground(Color.LIGHT_GRAY);
            }
            
            return this;
        }
    }

    private void showPage(int index) {
        if (index < 0 || index >= document.getNumberOfPages())
            return;

        try {
            BufferedImage finalImage;

            // Modo página única
            finalImage = renderPageImage(index);
            statusLabel.setText(" Página " + (index + 1) + " de " + document.getNumberOfPages() + " ");

            pageLabel.setIcon(new ImageIcon(finalImage));
            currentPage = index;
            
            // Sincroniza o minimap
            thumbnailList.setSelectedIndex(index);
            thumbnailList.ensureIndexIsVisible(index);

            // Volta o scroll para o topo
            SwingUtilities.invokeLater(
                    () -> ((JScrollPane) pageLabel.getParent().getParent()).getVerticalScrollBar().setValue(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage renderPageImage(int index) throws IOException {
        // === QUALIDADE "ACROBAT" (Super Sampling + Area Averaging) ===

        // 1. Renderiza o PDF com alta resolução (4x DPI)
        float screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
        float renderScale = 4.0f; // Aumentado para 4x para máxima qualidade
        BufferedImage highResImage = renderer.renderImageWithDPI(index, screenDpi * renderScale, ImageType.RGB);

        // 2. Calcula o tamanho final
        int displayWidth = (int) (highResImage.getWidth() / renderScale * zoomFactor);
        int displayHeight = (int) (highResImage.getHeight() / renderScale * zoomFactor);

        // 3. Redimensiona usando Area Averaging (Melhor algoritmo para redução de texto)
        // Nota: getScaledInstance é mais lento que Graphics2D, mas a qualidade é superior para texto.
        Image scaledImage = highResImage.getScaledInstance(displayWidth, displayHeight, Image.SCALE_AREA_AVERAGING);

        // 4. Converte de volta para BufferedImage para exibir no JLabel
        BufferedImage finalImage = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return finalImage;
    }
}
