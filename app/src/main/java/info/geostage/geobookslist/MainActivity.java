package info.geostage.geobookslist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Context variable so it can be referenced in other activities
     */
    public static Context context;

    /**
     * EditText for user's search
     */
    private EditText searchEditText;

    /**
     * Button which will call BookActivity
     */
    Button searchButton;

    /**
     * Button clearing EditText
     */
    Button clearButton;

    /**
     * String containing the user's input
     */
    public static String userSearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Find the {@link EditText} object in the layout with ID key_word_field
        searchEditText = (EditText) findViewById(R.id.key_word_field);

        // Find the {@link Button} object in the layout with ID clear_button
        clearButton = (Button) findViewById(R.id.clear_button);

        // Find the {@link Button} object in the layout with ID search_button
        searchButton = (Button) findViewById(R.id.search_button);

        // Set OnClickListeners
        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        // Disable and hide clear button if the EditText is empty
        if (searchEditText.getText().length() == 0) {
            clearButton.setEnabled(false);
            clearButton.setVisibility(View.INVISIBLE);
        } else {
            clearButton.setEnabled(true);
            clearButton.setVisibility(View.VISIBLE);
        }

        // Set TextChanged Listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If there is text in the EditText, show the clear button, if not, hide it
                if (s.toString().trim().length() == 0) {
                    clearButton.setEnabled(false);
                    clearButton.setVisibility(View.INVISIBLE);
                } else {
                    clearButton.setEnabled(true);
                    clearButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Called when the user taps the Search button
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_button:
                searchEditText.setText("");
                break;
            case R.id.search_button:
                // Save user's input
                userSearchInput = searchEditText.getText().toString();
                // Start BookActivity in order to show the query result
                Intent intent = new Intent(MainActivity.this, BookActivity.class);
                startActivity(intent);
                break;
        }
    }
}
