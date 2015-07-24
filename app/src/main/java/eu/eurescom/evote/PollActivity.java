package eu.eurescom.evote;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.eurescom.evote.Model.Option;
import eu.eurescom.evote.Model.Poll;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class PollActivity extends Activity {

    private TextView mTitleLabel;
    private StickyListHeadersListView mListView;
    private Button mSubmitButton;
    private APIClient mAPIClient;
    private Poll mPoll;
    private String mToken;
    private PollListAdapter mAdapter;

    private HashSet<Option> votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        mPoll = (Poll) getIntent().getSerializableExtra("poll");
        mToken = getIntent().getStringExtra("token");
        Log.d("", "Token: " + mToken);
        Log.d("", "Poll: " + mPoll.getTitle());

        votes = new HashSet<>();

        // Port 82 on machine hosting the emulator.
        String url = "http://10.0.2.2:82";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        mAPIClient = restAdapter.create(APIClient.class);

        mTitleLabel = (TextView) findViewById(R.id.titleLabel);
        mTitleLabel.setText(mPoll.getTitle());
        mListView = (StickyListHeadersListView) findViewById(R.id.optionsList);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mAdapter = new PollListAdapter(this, R.layout.cell_option, R.id.optionLabel, mPoll.getOptions());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int max = mPoll.getSelectMax();
                Option option = mPoll.getOptions().get(position);
                if (votes.contains(option)) {
                    votes.remove(option);
                } else if (max == 1) {
                    votes.clear();
                    votes.add(option);
                } else if (votes.size() < max) {
                    votes.add(option);
                } else {
                    String optionString = max > 1 ? "options" : "option";
                    new AlertDialog.Builder(PollActivity.this)
                            .setTitle("Not Allowed")
                            .setMessage("You cannot select more than " + max + " " + optionString + ".")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Intentionally empty.
                                }
                            })
                            .show();
                }
                updateUI();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressHUD = ProgressDialog.show(PollActivity.this, null, "Loading");
                JsonObject json = votesToJSON();
                mAPIClient.submitVoteForCode(mToken, json, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        progressHUD.hide();
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if (success) {
                            new AlertDialog.Builder(PollActivity.this)
                                    .setMessage("Your vote has been submitted.")
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            // Close the poll activity.
                                            finish();
                                        }
                                    })
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Close the poll activity.
                                            finish();
                                        }
                                    })
                                    .show();
                        } else{
                            JsonObject error = jsonObject.getAsJsonObject("error");
                            String message = error.get("message").getAsString();
                            new AlertDialog.Builder(PollActivity.this)
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
                        String message = "Something went wrong, try again later.";
                        new AlertDialog.Builder(PollActivity.this)
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
        });
    }

    private JsonObject votesToJSON() {
        int[] optionIds = new int[votes.size()];
        int index = 0;
        for (Option option : votes) {
            optionIds[index] = option.getId();
            index++;
        }
        JsonElement arr = new Gson().toJsonTree(optionIds);
        JsonObject json = new JsonObject();
        json.add("options", arr);
        return json;
    }


    private void updateUI() {
        mAdapter.notifyDataSetChanged();
    }

    // This class acts as a kind of cache for the list rows to make scrolling more smooth.
    // This is a common thing to do in Android...
    private static class OptionCellHolder {
        TextView option;
        CheckBox checkBox;
    }

    private class PollListAdapter extends ArrayAdapter<Option> implements StickyListHeadersAdapter {
        public PollListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Option> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        // Make sure the checkboxes are checked appropriately.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OptionCellHolder viewHolder;
            if(convertView == null) {
                // Inflate the layout.
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.cell_option, parent, false);

                // Create a view holder.
                viewHolder = new OptionCellHolder();
                viewHolder.option = (TextView) convertView.findViewById(R.id.optionLabel);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

                // Store the holder in the view.
                convertView.setTag(viewHolder);
            }
            else {
                // We've avoided calling findViewById() every time, which saves a lot of compute.
                viewHolder = (OptionCellHolder) convertView.getTag();
            }

            Option option = mPoll.getOptions().get(position);
            viewHolder.option.setText(option.getText());
            if(votes.contains(option)) {
                viewHolder.checkBox.setChecked(true);
            }
            else {
                viewHolder.checkBox.setChecked(false);
            }
            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(PollActivity.this);
            convertView = inflater.inflate(R.layout.header_poll, viewGroup, false);
            TextView label = (TextView) convertView.findViewById(R.id.queryLabel);
            label.setText(mPoll.getQuestion());
            return convertView;
        }

        @Override
        public long getHeaderId(int i) {
            return 0;
        }
    }
}
