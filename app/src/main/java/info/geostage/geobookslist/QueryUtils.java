package info.geostage.geobookslist;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper methods related to requesting and receiving book data from Google books.
 */
public final class QueryUtils {

    private static final String KEY_IMAGELINKS = "imageLinks";
    private static final String KEY_SMALLTHUMBNAIL = "smallThumbnail";
    private static final String KEY_VOLUMEINFO = "volumeInfo";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_PAGECOUNT = "pageCount";
    private static final String KEY_DATE = "publishedDate";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PREVIEWLINK = "previewLink";

    public static String errorMessage = null;

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static Context context = MainActivity.context;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google books and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // Variables for JSON parsing
            JSONObject imageLinks;
            String smallThumbnail;
            String title;
            String author;
            JSONArray authors;
            int pageCount;
            String date;
            String description;
            String previewLink;

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject(KEY_VOLUMEINFO);
                // Check if key "title" exists and if yes, return value
                if (volumeInfo.has(KEY_TITLE)) {
                    title = volumeInfo.getString(KEY_TITLE);
                } else {
                    title = null;
                }
                // Check if key "authors" exists and if yes, return value
                if (volumeInfo.has(KEY_AUTHORS)) {
                    authors = volumeInfo.getJSONArray(KEY_AUTHORS);
                    author = authors.getString(0);
                } else {
                    author = null;
                }

                // Check if key "description" exists and if yes, return value
                if (volumeInfo.has(KEY_DESCRIPTION)) {
                    description = volumeInfo.getString(KEY_DESCRIPTION);
                } else {
                    description = null;
                }

                // Check if key "pageCount" exists and if yes, return value
                if (volumeInfo.has(KEY_PAGECOUNT)) {
                    pageCount = volumeInfo.getInt(KEY_PAGECOUNT);
                } else {
                    pageCount = 0;
                }

                // Check if key "imageLinks" exists and if yes, return value
                if (volumeInfo.has(KEY_IMAGELINKS)) {
                    imageLinks = volumeInfo.getJSONObject(KEY_IMAGELINKS);
                    smallThumbnail = imageLinks.getString(KEY_SMALLTHUMBNAIL);
                } else {
                    smallThumbnail = null;
                }

                // Check if key "publishedDate" exists and if yes, return value
                if (volumeInfo.has(KEY_DATE)) {
                    date = volumeInfo.getString(KEY_DATE);
                } else {
                    date = null;
                }

                // Check if key "previewLink" exists and if yes, return value
                if (volumeInfo.has(KEY_PREVIEWLINK)) {
                    previewLink = volumeInfo.getString(KEY_PREVIEWLINK);
                } else {
                    previewLink = null;
                }

                // Create the BookItem object and add it to the ArrayList
                Book book = new Book(smallThumbnail, previewLink, title, author,
                        pageCount, date, description);
                books.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
            errorMessage = "Problem parsing the Book JSON results. JSONExcelption: " + e;
        }

        // Return the list of Books
        return books;
    }

}