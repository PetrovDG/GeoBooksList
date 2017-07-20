package info.geostage.geobookslist;

/**
 * An {@link Book} object contains information related to a single book.
 */
public class Book {


    /** Url for the book's thumbnail */
    private String mSmallThumbnailURL;

    /** Url for the book's preview */
    private String mPreviewURL;

    /** Book Title */
    private String mTitle;

    /** Book Author */
    private String mAuthor;

    /** Book Page Count */
    private int mPageCount;

    /** Book published date */
    private String mDate;

    /** Book Description */
    private String mDescription;

    /**
     * Constructs a new {@link Book} object.
     * @param smallThumbnailURL is the thumbnail which will be shown as the book's image
     * @param previewURL is the link to the book's previewURL page
     * @param title is the book's title
     * @param author is the book's author
     * @param pageCount is the book's number of pages
     * @param date is the book's published date
     * @param description is a short description of the book
     */
    public Book(String smallThumbnailURL, String previewURL, String title, String author, int
            pageCount, String date, String description) {
        mSmallThumbnailURL = smallThumbnailURL;
        mPreviewURL = previewURL;
        mTitle = title;
        mAuthor = author;
        mPageCount = pageCount;
        mDate = date;
        mDescription = description;
    }

    public String getmSmallThumbnailURL() {
        return mSmallThumbnailURL;
    }

    public String getmPreviewURL() {
        return mPreviewURL;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPageCount() {
        return String.valueOf(mPageCount);
    }

    public String getmDate() {
        return mDate;
    }

    public String getmDescription() {
        return mDescription;
    }
}
