package com.example.harry.qrecruiter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private TextView textView;
    private String positionChosen;
    private ArrayList<String> arrayListPositions;
    private HashMap<String, String> mapPositions;
    private ArrayAdapter<String> arrayAdapter;
    private String URLURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        URLURL = getIntent().getStringExtra("URL");
        positionChosen = "";

        arrayListPositions = new ArrayList<String>();
        arrayListPositions.add("Front End Engineering");
        arrayListPositions.add("Back End Engineering");
        arrayListPositions.add("Undergraduate Internship (Summer)");
        arrayListPositions.add("Graduate Internship (Summer)");

        mapPositions = new HashMap<>();
        mapPositions.put("Front End Engineering", "frontendEngineering");
        mapPositions.put("Back End Engineering", "backendEngineering");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListPositions);

        textView = (TextView) findViewById(R.id.textView_position);

        listView = (ListView) findViewById(R.id.listView_positions);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionChosen = (String) parent.getItemAtPosition(position);
                textView.setText(positionChosen);
                //Toast.makeText(HomeActivity.this, mapPositions.get(positionChosen), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickScan (View view){
        if (positionChosen.equals("")){
            Toast.makeText(this, "Please select a position before proceeding", Toast.LENGTH_SHORT).show();
        }else if (!mapPositions.containsKey(positionChosen)){
            Toast.makeText(this, "That position is unavailable at the moment", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("position", mapPositions.get(positionChosen));
            intent.putExtra("URL", URLURL);
            startActivity(intent);
        }
    }
}
