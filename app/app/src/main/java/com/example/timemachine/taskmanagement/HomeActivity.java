package com.example.timemachine.taskmanagement;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemachine.taskmanagement.Model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    // recycler view
    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String random_uid =  mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(random_uid);

        // recycler view grabbing
        recycler_view = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(layoutManager);

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
                final AlertDialog dialog = myDialog.create();

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

                        String id = mDatabase.push().getKey();
                        String date = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(mTitle,mNote,date,id);
                        mDatabase.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Data has been inserted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Problem occur!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View v;

        public MyViewHolder(View itemView) {
            super(itemView);
            v = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = v.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note) {
            TextView mNote = v.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = v.findViewById(R.id.date);
            mDate.setText(date);
        }
    }
}
