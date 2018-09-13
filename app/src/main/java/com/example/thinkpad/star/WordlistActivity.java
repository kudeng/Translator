package com.example.thinkpad.star;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WordlistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        Map<String, String> map = new HashMap<String, String>();
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        map = serializableMap.getMap();
        Iterator it = map.entrySet().iterator();

        EditText starList = findViewById(R.id.StarView);
        System.out.println(map);
        while(it.hasNext())
        {
            starList.append(it.next().toString());
            starList.append("\n");
        }
    }
}
