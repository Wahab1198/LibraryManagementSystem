import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Graphical user interface for the Library Book Management System.
 *
 * @author OEL Abdul Wahab
 */
public class LibraryGUI extends JFrame {
    private final Library library;
    private final JTextField idField;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JComboBox<String> statusBox;
    private final DefaultTableModel tableModel;
    private final JTable bookTable;

    /** Builds and displays the library management window. */
    public LibraryGUI() {
        try {
            library = new Library();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Storage Error", JOptionPane.ERROR_MESSAGE);
            throw ex;
        }
        setTitle("Library Book Management System");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        statusBox = new JComboBox<>(new String[]{"Available", "Issued", "Reserved"});
        inputPanel.add(new JLabel("Book ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Book Title:")); inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:")); inputPanel.add(authorField);
        inputPanel.add(new JLabel("Status:")); inputPanel.add(statusBox);

        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                int row = bookTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(row, 0).toString());
                titleField.setText(tableModel.getValueAt(row, 1).toString());
                authorField.setText(tableModel.getValueAt(row, 2).toString());
                statusBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                idField.setEditable(false);
            }
        });

        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton clearButton = new JButton("Clear");
        JButton viewButton = new JButton("View All Books");
        addButton.addActionListener(event -> addBook());
        updateButton.addActionListener(event -> updateBook());
        deleteButton.addActionListener(event -> deleteBook());
        clearButton.addActionListener(event -> clearFields());
        viewButton.addActionListener(event -> viewAllBooks());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton); buttonPanel.add(updateButton); buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton); buttonPanel.add(clearButton);
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        refreshTable();
    }

    /** Adds a book after validation. */
    private void addBook() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String status = statusBox.getSelectedItem().toString();
            if (library.findBook(id) != null) { showError("A book with this ID already exists."); return; }
            if (!library.addBook(new Book(id, title, author, status))) return;
            refreshTable(); clearFields();
            JOptionPane.showMessageDialog(this, "Book added and saved successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) { showError(ex.getMessage()); }
    }

    /** Updates a selected or entered book. */
    private void updateBook() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String status = statusBox.getSelectedItem().toString();
            if (!library.updateBook(id, title, author, status)) { showError("No book found with this ID."); return; }
            refreshTable(); clearFields();
            JOptionPane.showMessageDialog(this, "Book updated and saved successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) { showError(ex.getMessage()); }
    }

    /** Deletes a book by ID. */
    private void deleteBook() {
        String id = idField.getText().trim();
        if (id.isEmpty()) { showError("Enter or select a Book ID first."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete book " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (!library.deleteBook(id)) { showError("No book found with this ID."); return; }
            refreshTable(); clearFields();
            JOptionPane.showMessageDialog(this, "Book deleted successfully.");
        } catch (IllegalStateException ex) { showError(ex.getMessage()); }
    }

    /** Displays all books, including an empty-library message. */
    private void viewAllBooks() {
        refreshTable();
        if (library.isEmpty()) JOptionPane.showMessageDialog(this, "The library is currently empty.");
    }

    /** Refreshes the table from saved library records. */
    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Book> books = library.getAllBooks();
        for (Book book : books) tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getStatus()});
    }

    /** Clears all input fields. */
    private void clearFields() {
        idField.setText(""); titleField.setText(""); authorField.setText("");
        statusBox.setSelectedIndex(0); idField.setEditable(true); bookTable.clearSelection();
    }

    /** Displays an error message.
     * @param message error description
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Starts the application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                LibraryGUI window = new LibraryGUI();
                window.setVisible(true);
            } catch (IllegalStateException ex) {
                System.exit(1);
            }
        });
    }
}
