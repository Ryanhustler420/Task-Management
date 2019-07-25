package com.example.timemachine.taskmanagement;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Taskmanager.io");

        fab = findViewById(R.id.fab_btn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Toast.makeText(getApplicationContext(), "add new task", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
            View v = inflater.inflate(R.layout.custom_input_field, null);
            myDialog.setView(v);
            AlertDialog dialog = myDialog.create();

            final EditText title = v.findViewById(R.id.edt_title);
            final EditText note = v.findViewById(R.id.edt_note);

            Button btnSave = v.findViewById(R.id.btn_save);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mTitle = title.getText().toString().trim();
                    String mNote = note.getText().toString().trim();

                    if(TextUtils.isEmpty(mTitle)) {
                        title.setError("Add Title");
                        return;
                    }

                    if(TextUtils.isEmpty(mNote)){
                        note.setError("Add Description");
                        return;
                    }

                    // Field Checked Successfully Not Perform Fire base actions

                }
            });

            dialog.show();
            }
        });
    }
}
