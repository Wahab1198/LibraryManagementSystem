import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages books and permanently stores them in data/books.dat.
 *
 * @author OEL Abdul Wahab
 */
public class Library {
    private final ArrayList<Book> books;
    private final Path dataFile;
    private static final int FILE_VERSION = 1;
    private static final Set<String> VALID_STATUSES = new HashSet<>(
            Arrays.asList("Available", "Issued", "Reserved"));

    /** Loads previously saved books, if a data file exists. */
    public Library() {
        books = new ArrayList<>();
        dataFile = Paths.get("data", "books.dat");
        loadBooks();
    }

    /** Adds a valid book and saves it permanently.
     * @param book book to add
     * @return true if added; false if its ID already exists
     */
    public boolean addBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null.");
        validateBook(book.getId(), book.getTitle(), book.getAuthor(), book.getStatus());
        if (findBook(book.getId()) != null) return false;
        books.add(book);
        saveOrRollback(() -> books.remove(book));
        return true;
    }

    /** Finds a book by ID.
     * @param id ID to search for
     * @return matching book, or null if not found
     */
    public Book findBook(String id) {
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(id)) return book;
        }
        return null;
    }

    /** Updates an existing book and saves the changes.
     * @param id ID of the book
     * @param title new title
     * @param author new author
     * @param status new status
     * @return true if updated; false if not found
     */
    public boolean updateBook(String id, String title, String author, String status) {
        validateBook(id, title, author, status);
        Book book = findBook(id);
        if (book == null) return false;
        String oldTitle = book.getTitle();
        String oldAuthor = book.getAuthor();
        String oldStatus = book.getStatus();
        book.setTitle(title);
        book.setAuthor(author);
        book.setStatus(status);
        saveOrRollback(() -> {
            book.setTitle(oldTitle);
            book.setAuthor(oldAuthor);
            book.setStatus(oldStatus);
        });
        return true;
    }

    /** Deletes a book and saves the change.
     * @param id ID of the book to delete
     * @return true if deleted; false if not found
     */
    public boolean deleteBook(String id) {
        Book book = findBook(id);
        if (book == null) return false;
        int index = books.indexOf(book);
        books.remove(book);
        saveOrRollback(() -> books.add(index, book));
        return true;
    }

    /** @return a copy of all books */
    public ArrayList<Book> getAllBooks() { return new ArrayList<>(books); }

    /** @return true if the library is empty */
    public boolean isEmpty() { return books.isEmpty(); }

    private void validateBook(String id, String title, String author, String status) {
        String cleanId = id == null ? "" : id.trim();
        String cleanTitle = title == null ? "" : title.trim();
        String cleanAuthor = author == null ? "" : author.trim();
        if (!cleanId.matches("[A-Za-z0-9][A-Za-z0-9_-]{1,19}")) {
            throw new IllegalArgumentException("Book ID must be 2-20 characters and contain only letters, numbers, hyphens or underscores.");
        }
        if (cleanTitle.length() < 2 || cleanTitle.length() > 100 ||
                !cleanTitle.matches(".*[\\p{L}\\p{N}].*") || cleanTitle.replaceAll("[^\\p{L}\\p{N}]", "").length() < 2) {
            throw new IllegalArgumentException("Enter a meaningful title (2-100 characters). A title cannot contain only dots or symbols.");
        }
        if (cleanAuthor.length() < 2 || cleanAuthor.length() > 80 ||
                !cleanAuthor.matches(".*\\p{L}.*") || cleanAuthor.replaceAll("[^\\p{L}]", "").length() < 2) {
            throw new IllegalArgumentException("Enter a valid author name (2-80 characters). Numbers-only or symbols-only names are not allowed.");
        }
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Select a valid status: Available, Issued or Reserved.");
        }
    }

    private void loadBooks() {
        if (!Files.exists(dataFile)) return;
        try (DataInputStream input = new DataInputStream(Files.newInputStream(dataFile))) {
            int version = input.readInt();
            if (version != FILE_VERSION) throw new IOException("Unsupported library data file version.");
            int count = input.readInt();
            if (count < 0 || count > 100000) throw new IOException("Invalid number of books in data file.");
            for (int i = 0; i < count; i++) {
                String id = input.readUTF();
                String title = input.readUTF();
                String author = input.readUTF();
                String status = input.readUTF();
                validateBook(id, title, author, status);
                if (findBook(id) != null) throw new IOException("Duplicate book ID in saved data: " + id);
                books.add(new Book(id, title, author, status));
            }
        } catch (IOException | IllegalArgumentException ex) {
            books.clear();
            throw new IllegalStateException("Could not load saved books. The data file may be damaged. " + ex.getMessage(), ex);
        }
    }

    private void saveBooks() throws IOException {
        Path parent = dataFile.getParent();
        Files.createDirectories(parent);
        Path temporaryFile = parent.resolve("books.tmp");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(temporaryFile))) {
            output.writeInt(FILE_VERSION);
            output.writeInt(books.size());
            for (Book book : books) {
                output.writeUTF(book.getId());
                output.writeUTF(book.getTitle());
                output.writeUTF(book.getAuthor());
                output.writeUTF(book.getStatus());
            }
        }
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void saveOrRollback(Runnable rollback) {
        try {
            saveBooks();
        } catch (IOException ex) {
            rollback.run();
            throw new IllegalStateException("Could not save the library data. Check that the project folder is writable. " + ex.getMessage(), ex);
        }
    }
}
