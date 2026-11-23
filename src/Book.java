/**
 * Represents a book in the library management system.
 *
 * @author OEL Abdul Wahab
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

    /** @return book ID */
    public String getId() { return id; }

    /** @return book title */
    public String getTitle() { return title; }

    /** @param title new book title */
    public void setTitle(String title) { this.title = title; }

    /** @return book author */
    public String getAuthor() { return author; }

    /** @param author new author name */
    public void setAuthor(String author) { this.author = author; }

    /** @return current book status */
    public String getStatus() { return status; }

    /** @param status new book status */
    public void setStatus(String status) { this.status = status; }
}
