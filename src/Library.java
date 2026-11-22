import java.util.ArrayList;

/**
 * Manages books stored in the library.
 *
 * @author OEL Student
 */
public class Library {
    private final ArrayList<Book> books;

    /**
     * Creates an empty library.
     */
    public Library() {
        books = new ArrayList<>();
    }

    /**
     * Adds a book if its ID is not already present.
     *
     * @param book book to add
     * @return true if added; false if the ID already exists
     */
    public boolean addBook(Book book) {
        if (findBook(book.getId()) != null) {
            return false;
        }
        books.add(book);
        return true;
    }

    /**
     * Finds a book using its ID.
     *
     * @param id ID to search for
     * @return matching book, or null if not found
     */
    public Book findBook(String id) {
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }

    /**
     * Updates the title, author, and status of an existing book.
     *
     * @param id ID of the book to update
     * @param title new title
     * @param author new author
     * @param status new status
     * @return true if updated; false if the ID does not exist
     */
    public boolean updateBook(String id, String title, String author, String status) {
        Book book = findBook(id);
        if (book == null) {
            return false;
        }
        book.setTitle(title);
        book.setAuthor(author);
        book.setStatus(status);
        return true;
    }

    /**
     * Deletes a book using its ID.
     *
     * @param id ID of the book to delete
     * @return true if deleted; false if the ID does not exist
     */
    public boolean deleteBook(String id) {
        Book book = findBook(id);
        if (book == null) {
            return false;
        }
        books.remove(book);
        return true;
    }

    /**
     * Returns all books in the library.
     *
     * @return list of books
     */
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Checks whether the library has no books.
     *
     * @return true if empty; otherwise false
     */
    public boolean isEmpty() {
        return books.isEmpty();
    }
}
