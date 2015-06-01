package eu.eurescom.evote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashSet;
import java.util.List;

import eu.eurescom.evote.Model.Poll;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class PollActivity extends Activity {

    private StickyListHeadersListView mListView;
    private Button mSubmitButton;
    private APIClient mAPIClient;
    private Poll mPoll;
    private PollListAdapter mAdapter;

    private HashSet<Integer> votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        votes = new HashSet<>();

        // Port 5000 on machine hosting the emulator.
        String url = "http://10.0.2.2:5000";
        //String url = "https://evoteapi.herokuapp.com";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        mAPIClient = restAdapter.create(APIClient.class);

        mListView = (StickyListHeadersListView) findViewById(R.id.optionsList);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        final String code = getIntent().getStringExtra("code");
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject json = new JsonObject();
                Gson gson = new Gson();
                json.add("code", new JsonPrimitive(code));
                json.add("votes", new JsonPrimitive(gson.toJson(votes)));
                mAPIClient.submitVoteForCode(json, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            String message = jsonObject.get("message").getAsString();
                            new AlertDialog.Builder(PollActivity.this)
                                    .setTitle("Success")
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else {
                            String message = jsonObject.get("message").getAsString();
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
                        new AlertDialog.Builder(PollActivity.this)
                                .setTitle("Error")
                                .setMessage("Something went wrong. Please try again later")
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
        mPoll = getIntent().getExtras().getParcelable("poll");
        mAdapter = new PollListAdapter(this, R.layout.cell_option, R.id.optionLabel, mPoll.getOptions());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("", "Clicked item at position " + position);

                int select = mPoll.getSelect();
                // If this is a single choice poll, move the selection.
                if(select == 1 ) {
                    if(votes.contains(position)) {
                        votes.remove(position);
                    }
                    else {
                        votes.clear();
                        votes.add(position);
                    }
                }
                else {
                    // Prevent the user from selecting more options than allowed.
                    int count = votes.size();
                    if(count < select || votes.contains(position)) {
                        if(votes.contains(position)) {
                            votes.remove(position);
                        }
                        else {
                            votes.add(position);
                        }
                    }
                    else {
                        // User tried to select more options than allowed.
                        new AlertDialog.Builder(PollActivity.this)
                                .setTitle("Not allowed")
                                .setMessage("You cannot select more than " + select + " options.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                }
                updateUI();
            }
        });
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

    private class PollListAdapter extends ArrayAdapter<String> implements StickyListHeadersAdapter {
        public PollListAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
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

            viewHolder.option.setText(mPoll.getOptions().get(position));
            if(votes.contains(position)) {
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
            label.setText(mPoll.getQuery());
            return convertView;
        }

        @Override
        public long getHeaderId(int i) {
            return 0;
        }
    }
}
