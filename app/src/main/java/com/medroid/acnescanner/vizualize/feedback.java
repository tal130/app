package com.medroid.acnescanner.vizualize;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.medroid.acnescanner.BaseActivity;
import com.medroid.acnescanner.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class feedback extends BaseActivity {
    EditText text= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Button send = (Button) findViewById(R.id.button);
         text= (EditText) findViewById(R.id.editText);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject review= new ParseObject("review");
                ParseUser user = ParseUser.getCurrentUser();
                review.put("user", user);
                if (text.getText()!= null) {
                    review.put("text", text.getText().toString());
                    review.saveInBackground();
                    Toast.makeText(getApplicationContext(), "THANK YOU!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
