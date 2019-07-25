package com.example.timemachine.taskmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemachine.taskmanagement.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
    private ProgressDialog mProgress;
    private Boolean listCreated = false;

    // recycler view
    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading Tasks...");
        mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String random_uid =  mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(random_uid);

        mDatabase.keepSynced(true);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // Minimize the app if this treated as last in the stack, which could be possible!
                Intent resIntent = getIntent();
                String previousActivity = resIntent.getStringExtra("FROM");
                if(previousActivity.equals("Login")) {
                    minimizeApp();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!listCreated) {
            initTaskList();
        }
    }

    private void initTaskList() {
        listCreated = true;
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
            }

            @Override
            public int getItemCount() {
                if(mProgress.isShowing()) {
                    mProgress.dismiss();
                }
                if(super.getItemCount() > 0) {
                    Toast.makeText(getApplicationContext(), super.getItemCount() + " Task(s) Available", Toast.LENGTH_SHORT).show();
                }
                return super.getItemCount();
            }
        };

        recycler_view.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
