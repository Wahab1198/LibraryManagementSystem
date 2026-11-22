/**
 * Represents a book in the library management system.
 *
 * @author OEL Student
 */
public class Book {
    private final String id;
    private String title;
    private String author;
    private String status;

    /**
     * Creates a book with the supplied information.
     *
     * @param id unique book ID
     * @param title book title
     * @param author book author
     * @param status current book status
     */
    public Book(String id, String title, String author, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    /**
     * Gets the book ID.
     *
     * @return book ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the book title.
     *
     * @return book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the book title.
     *
     * @param title new book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author name.
     *
     * @return author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author name.
     *
     * @param author new author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the current status.
     *
     * @return book status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status.
     *
     * @param status new book status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
