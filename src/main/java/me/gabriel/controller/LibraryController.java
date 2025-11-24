package me.gabriel.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import me.gabriel.adapter.LocalDateAdapter;
import me.gabriel.model.BookModel;
import me.gabriel.model.Status;
import me.gabriel.view.BookFormDialog;
import me.gabriel.view.DashboardDialog;
import me.gabriel.view.MainFrame;
import me.gabriel.view.PdfViewer;

public class LibraryController {

    private final MainFrame view;
    private List<BookModel> books;
    private static final String SAVE_FILE = "library.json";
    private final Gson gson;

    public LibraryController(MainFrame view) {
        this.view = view;
        
        // Configura a instância do Gson com o adaptador para LocalDate
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        this.books = loadLibrary();
        this.view.setController(this); // Conecta a View ao Controller
        this.view.displayBooks(this.books); // Pede para a view exibir os livros carregados
    }

    private void saveLibrary() {
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            gson.toJson(books, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar a biblioteca: " + e.getMessage());
        }
    }

    private List<BookModel> loadLibrary() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type bookListType = new TypeToken<ArrayList<BookModel>>() {
            }.getType();
            List<BookModel> loadedBooks = gson.fromJson(reader, bookListType);
            return loadedBooks != null ? loadedBooks : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>(); // Retorna lista vazia se o arquivo não existe ou está corrompido
        }
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
                        dialog.getStatus(),
                        dialog.getTitulo(),
                        dialog.getAutor(),
                        dialog.getAno(),
                        dialog.getCategoria(),
                        fileToSave);

                try (PDDocument document = Loader.loadPDF(fileToSave)) {
                    newBook.setTotalPages(document.getNumberOfPages());
                } catch (IOException e) {
                    System.err.println("Erro ao ler o arquivo PDF: " + e.getMessage());
                }

                books.add(newBook);
                System.out.println("Livro importado: " + newBook);

                // Atualiza a tela
                view.addBookToView(newBook);
                saveLibrary();
            }
        }

    }

    public void openBook(int index) {
        if (index >= 0 && index < books.size()) {
            BookModel book = books.get(index);
            File file = book.getFilePath();

            if (file != null && file.exists()) {
                // Abre o visualizador interno passando a página salva
                PdfViewer viewer = new PdfViewer(file, book.getPaginaAtual());

                // Salva a página quando fechar
                viewer.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        book.setPaginaAtual(viewer.getCurrentPage());
                        System.out.println("Progresso salvo: Página " + book.getPaginaAtual());
                        saveLibrary(); // SALVA O PROGRESSO
                        view.displayBooks(books); // ATUALIZA A TABELA PRINCIPAL
                    }
                });

                viewer.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view, "Arquivo não encontrado: " + (file != null ? file.getAbsolutePath() : "caminho nulo"));
            }
        }
    }

    public void updateBookStatus(int index, Status newStatus) {
        if (index >= 0 && index < books.size()) {
            BookModel book = books.get(index);
            book.setStatus(newStatus);
            System.out.println("Status atualizado para: " + newStatus.getDescricao());
            saveLibrary(); // SALVA A MUDANÇA DE STATUS
        }
    }

    public void showDashboard() {
        // Calcula as estatísticas usando Stream API
        Map<Status, Long> stats = books.stream()
                .collect(Collectors.groupingBy(BookModel::getStatus, Collectors.counting()));

        // Cria e exibe o diálogo do dashboard
        DashboardDialog dashboard = new DashboardDialog(view, stats);
        dashboard.setVisible(true);
    }
}
