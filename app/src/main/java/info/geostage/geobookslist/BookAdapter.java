package info.geostage.geobookslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * An {@link BookAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link Book} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context of the app
     * @param books is the list of books, which is the data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }
    /**
     * Returns a list view that displays information about the book at the given position
     * in the list of books.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View bookView = convertView;
        if (bookView == null) {
            bookView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }
        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Find the ImageView in the book_list_.xml layout with the ID image.
        ImageView thumbnailImageView = (ImageView) bookView.findViewById(R.id.image);
        // Check if there is a link to an image, otherwise use placeholder image.
        if (currentBook.getmSmallThumbnailURL() != null) {
            Picasso.with(getContext()).load(currentBook.getmSmallThumbnailURL()).into(thumbnailImageView);
        } else {
            thumbnailImageView.setImageResource(R.drawable.no_cover);
        }

        // Find the TextView in the book_list.xml layout with the ID book_title.
        TextView titleTextView = (TextView) bookView.findViewById(R.id.book_title);
        // Get the title from the currentBook object and set this text on the TextView.
        titleTextView.setText(currentBook.getmTitle());

        // Find the TextView in the book_list.xml layout with the ID book_author.
        TextView authorTextView = (TextView) bookView.findViewById(R.id.book_author);
        // Get the author from the currentBook object and set this text on the TextView.
        authorTextView.setText(currentBook.getmAuthor());

        // Find the TextView in the book_list.xml layout with the ID pageCount.
        TextView pageCountTextView = (TextView) bookView.findViewById(R.id.pageCount);
        // Get the page count from the currentBook object and set this text on the TextView.
        pageCountTextView.setText(currentBook.getmPageCount());

        // Find the TextView in the book_list.xml layout with the ID publishedDate.
        TextView dateTextView = (TextView) bookView.findViewById(R.id.publishedDate);
        // Get the published date from the currentBook object and set this text on the TextView.
        dateTextView.setText(currentBook.getmDate());

        // Find the TextView in the book_list.xml layout with the ID book_description.
        TextView descriptionTextView = (TextView) bookView.findViewById(R.id.book_description);
        // Get the description from the currentBook object and set this text on the TextView.
        descriptionTextView.setText(currentBook.getmDescription());

        return bookView;
    }

}
