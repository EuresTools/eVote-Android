package eu.eurescom.evote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import eu.eurescom.evote.Model.Option;
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

        // Port 82 on the machine hosting the emulator.
        String url = "http://10.0.2.2:82";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        mAPIClient = restAdapter.create(APIClient.class);
    }

    private Poll pollFromJson(JsonObject json_poll) {
        Poll poll = new Poll();
        poll.setTitle(json_poll.get("title").getAsString());
        poll.setQuestion(json_poll.get("question").getAsString());
        poll.setSelectMin(json_poll.get("select_min").getAsInt());
        poll.setSelectMax(json_poll.get("select_max").getAsInt());
        ArrayList<Option> options = new ArrayList<Option>();
        JsonArray json_options = json_poll.get("options").getAsJsonArray();
        for (JsonElement json_element : json_options) {
            JsonObject json_option = json_element.getAsJsonObject();
            Option option = new Option();
            option.setId(json_option.get("id").getAsInt());
            option.setText(json_option.get("text").getAsString());
            options.add(option);
        }
        poll.setOptions(options);
        return poll;
    }


    private void didPressSubmitButton() {
        Log.d("", "Pressed submit");

        final ProgressDialog progressHUD = ProgressDialog.show(this, null, "Loading");
        final String code = mCodeField.getText().toString();
        mAPIClient.getPollForCode(code, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                progressHUD.hide();
                boolean success = jsonObject.get("success").getAsBoolean();
                Log.d("", jsonObject.toString());
                if(success) {
                    Log.d("", "Success");
                    JsonObject json_poll = jsonObject.getAsJsonObject("data");
                    Poll poll = pollFromJson(json_poll);
                    Intent intent = new Intent(VoteCodeActivity.this, PollActivity.class);
                    intent.putExtra("poll", poll);
                    intent.putExtra("token", code);
                    startActivity(intent);
                    mCodeField.setText("");
                } else {
                    Log.d("", "No Success");
                    JsonObject error = jsonObject.getAsJsonObject("error");
                    String message = error.get("message").getAsString();
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
                progressHUD.hide();
                Log.d("", "Failure");
                Log.d("", error.toString());
                String message = "";
                if (error.getResponse().getStatus() == 401) {
                    Log.d("", "Failed because of invalid token");
                    message = "Invalid voting code";
                } else {
                    Log.d("", "Failed because of unknown stuff");
                    message = "Something went wrong";
                }
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
        });
    }
}
