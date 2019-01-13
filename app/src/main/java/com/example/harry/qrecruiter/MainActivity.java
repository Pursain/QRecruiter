package com.example.harry.qrecruiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickRecruiting(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("URL", ((TextView)findViewById(R.id.editText_URL)).getText().toString());
        startActivity(intent);
    }
}
