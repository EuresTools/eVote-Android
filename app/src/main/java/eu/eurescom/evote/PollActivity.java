package eu.eurescom.evote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.HashSet;
import java.util.List;

import eu.eurescom.evote.Model.Poll;


public class PollActivity extends Activity {

    private ListView mListView;
    private Poll mPoll;
    private PollListAdapter mAdapter;

    private HashSet<Integer> votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        votes = new HashSet<>();
        mListView = (ListView) findViewById(R.id.optionsList);
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

    private class PollListAdapter extends ArrayAdapter<String> {
        public PollListAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        // Make sure the checkboxes are checked appropriately.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            checkBox.setChecked(votes.contains(position));
            return view;
        }
    }
}
