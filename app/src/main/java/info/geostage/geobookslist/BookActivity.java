package info.geostage.geobookslist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * The {@link BookActivity} is called by the search button to show the list of books.
 */
public class BookActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookActivity.class.getName();

    /**
     * URL for books data from the Google Books dataset
     */
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=geology";

    /**
     * Constant value for the book loader ID.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of books
     */
    private BookAdapter bookAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Progress Bar to use while running query
     */
    View progressBar;

    /** String to store the user's input */
    String userQuery;

    /** Final Query */
    private String finalQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Get the user's input
        userQuery = MainActivity.userSearchInput;

        // Call method to combine the user's input with the API's initial Query
        finalQuery = returnFinalQuery(userQuery);

        // Find the {@link ListView} object in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find the {@link EmptyView} object in the layout
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Create a new bookAdapter that takes the list of books as input
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(bookAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current book that was clicked on
                Book currentBookItem = bookAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookURL = Uri.parse(currentBookItem.getmPreviewURL());

                // Create new intent to view the book's URL
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookURL);

                // Start the intent
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Disable progressBar
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            // Display error
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public String returnFinalQuery(String keyWord) {
        String[] words = keyWord.split("\\s+");
        String wordsQuery = null;
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                wordsQuery = words[i];
            } else {
                wordsQuery = wordsQuery + "+" + words[i];
            }
        }
        Log.v("BookActivity", "Title: " + wordsQuery);

        if (TextUtils.isEmpty(wordsQuery)) {
            wordsQuery = "";
        } else {
            wordsQuery = "inKey:" + wordsQuery;
        }

        finalQuery = GOOGLE_BOOKS_REQUEST_URL + wordsQuery;
        Log.v("BookActivity", "Query: " + finalQuery);
        return finalQuery;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // Create new loader for the given URL
        return new BookLoader(this, finalQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Clear the adapter of previous book data
        bookAdapter.clear();
        if (QueryUtils.errorMessage != null) {
            Toast.makeText(MainActivity.context, QueryUtils.errorMessage, Toast.LENGTH_SHORT).show();
            QueryUtils.errorMessage = null;
        }
        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            bookAdapter.addAll(books);

            // Hide progress bar because the data has been loaded
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

        } else {
            // Hide progress bar because the data has been loaded
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            // Set empty state text to display "No books found!"
            mEmptyStateTextView.setText(R.string.no_books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        bookAdapter.clear();
    }


}