package eu.eurescom.evote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import eu.eurescom.evote.Model.Poll;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class VoteCodeActivity extends Activity {

    private EditText mCodeField;
    private Button mSubmitButton;
    private APIClient mAPIClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_code);

        mCodeField = (EditText) findViewById(R.id.codeField);
        mSubmitButton = (Button) findViewById(R.id.submitButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didPressSubmitButton();
            }
        });

        // Port 5000 on machine hosting the emulator.
        String url = "http://10.0.2.2:5000";
        //String url = "https://evoteapi.herokuapp.com";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        mAPIClient = restAdapter.create(APIClient.class);
    }

    private void didPressSubmitButton() {
        Log.d("", "Pressed submit");

        // This could be designed better using POJOs with Retrofit.
        final String code = mCodeField.getText().toString();
        mAPIClient.getPollForCode(code, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    Log.d("", "Valid");
                    JsonObject json_poll = jsonObject.getAsJsonObject("poll");
                    Poll poll = new Poll(json_poll);

                    // Start the poll activity.
                    Intent intent = new Intent(VoteCodeActivity.this, PollActivity.class);
                    intent.putExtra("poll", poll);
                    intent.putExtra("code", code);
                    startActivity(intent);
                } else {
                    // Display an alert with the error message.
                    String message = jsonObject.get("message").getAsString();
                    new AlertDialog.Builder(VoteCodeActivity.this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Intentionally empty.
                        }
                    })
                    .show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("", "Failure");
            }
        });
    }
}
