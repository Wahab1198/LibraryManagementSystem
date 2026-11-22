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
 * @author OEL Student
 */
public class LibraryGUI extends JFrame {
    private final Library library;
    private final JTextField idField;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JComboBox<String> statusBox;
    private final DefaultTableModel tableModel;
    private final JTable bookTable;

    /**
     * Builds and displays the library management window.
     */
    public LibraryGUI() {
        library = new Library();

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

        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Book Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusBox);

        tableModel = new DefaultTableModel(
            new String[]{"Book ID", "Title", "Author", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        refreshTable();
    }

    /**
     * Adds a book after validating the entered values.
     */
    private void addBook() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (!validateInput(id, title, author)) {
            return;
        }

        Book book = new Book(id, title, author, status);
        if (!library.addBook(book)) {
            showError("A book with this ID already exists.");
            return;
        }

        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Book added successfully.");
    }

    /**
     * Updates an existing book after validating the entered values.
     */
    private void updateBook() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (!validateInput(id, title, author)) {
            return;
        }

        if (!library.updateBook(id, title, author, status)) {
            showError("No book found with this ID.");
            return;
        }

        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Book updated successfully.");
    }

    /**
     * Deletes the book whose ID is entered.
     */
    private void deleteBook() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showError("Enter or select a Book ID first.");
            return;
        }

        if (!library.deleteBook(id)) {
            showError("No book found with this ID.");
            return;
        }

        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Book deleted successfully.");
    }

    /**
     * Checks required input fields.
     *
     * @param id book ID
     * @param title book title
     * @param author book author
     * @return true if the input is valid; otherwise false
     */
    private boolean validateInput(String id, String title, String author) {
        if (id.isEmpty()) {
            showError("Book ID cannot be empty.");
            return false;
        }
        if (title.isEmpty()) {
            showError("Book title cannot be empty.");
            return false;
        }
        if (author.isEmpty()) {
            showError("Author cannot be empty.");
            return false;
        }
        return true;
    }

    /**
     * Displays all books in the table and reports when the library is empty.
     */
    private void viewAllBooks() {
        refreshTable();
        if (library.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The library is currently empty.");
        }
    }

    /**
     * Refreshes the table using the current library records.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Book> books = library.getAllBooks();

        if (books.isEmpty()) {
            return;
        }

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(), book.getTitle(), book.getAuthor(), book.getStatus()
            });
        }
    }

    /**
     * Clears all input fields and resets the selected row.
     */
    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        statusBox.setSelectedIndex(0);
        idField.setEditable(true);
        bookTable.clearSelection();
    }

    /**
     * Displays an error message.
     *
     * @param message message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI window = new LibraryGUI();
            window.setVisible(true);
        });
    }
}
