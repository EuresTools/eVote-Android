package eu.eurescom.evote;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import eu.eurescom.evote.Model.Poll;


public class PollActivity extends Activity {

    private ListView mListView;
    private Poll mPoll;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        mListView = (ListView) findViewById(R.id.optionsList);
        mPoll = (Poll) getIntent().getExtras().getParcelable("poll");
        mAdapter = new ArrayAdapter(this, R.layout.cell_option, R.id.optionLabel, mPoll.getOptions());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PollActivity.this, "Clicked item at position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
