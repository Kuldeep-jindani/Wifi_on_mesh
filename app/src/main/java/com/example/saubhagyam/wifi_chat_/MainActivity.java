package com.example.saubhagyam.wifi_chat_;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView other_devices;
    ImageView edit;
    TextView profile_name;
    EditText profile_name_edittext;

    int name_changed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name_changed=0;

        other_devices= (ListView) findViewById(R.id.other_devices);
        edit= (ImageView) findViewById(R.id.edit);
        profile_name= (TextView) findViewById(R.id.profile_name);
        profile_name_edittext= (EditText) findViewById(R.id.profile_name_edittext);
    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    protected void onResume() {
        super.onResume();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_changed == 0) {
                    profile_name.setVisibility(View.GONE);
                    profile_name_edittext.setVisibility(View.VISIBLE);
                    edit.setImageDrawable(getResources().getDrawable(R.mipmap.ic_save));
                    name_changed=1;
                }else {
                    edit.setImageDrawable(getResources().getDrawable(R.mipmap.ic_edit));
                    profile_name.setVisibility(View.VISIBLE);
                    profile_name_edittext.setVisibility(View.GONE);
                    name_changed=0;
                }
            }
        });

        List<String> otherList=new ArrayList();
        for (int i=0;i<50;i++){
            otherList.add("Bot "+i);
        }

        ArrayAdapter adapter=new ArrayAdapter(getApplicationContext(),R.layout.custom_spinner_textview,otherList);
        other_devices.setAdapter(adapter);


        other_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(),ChatActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(false);
    }
}
