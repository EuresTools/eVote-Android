package eu.eurescom.evote;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class VoteCodeActivity extends Activity {

    private EditText mCodeField;
    private Button mSubmitButton;

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
    }

    private void didPressSubmitButton() {
    }
}
