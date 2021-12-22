package edu.ucdenver.kyleolson.cornellnotebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ListView extends AppCompatActivity {

    android.widget.ListView l;


//    String notes[]
//            = { "Using Sig Figs", "Reporting Uncertainty",
//            "Describing Data", "Interpreting and Creating Graphs",
//            "Null Hypothesis","Error Bars", "Dichotomous Key","Spreadsheets"};
    String name;
    TextView title;
    String key;
    String notes[];
    String uid;
    FloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            key = extras.getString("key");
            uid = extras.getString("uid");
            //notes = extras.getStringArrayList(notes);
            Log.v("Key",key);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        getSupportActionBar().hide();



        title = findViewById(R.id.listViewTitle);
        title.setText(name);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference folder = mDatabase.child(uid).child(key).child("notes");
        folder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> noteContent = new ArrayList<String>();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.v("Key", childDataSnapshot.getKey());
                    titles.add(childDataSnapshot.getKey().toString());
                    noteContent.add(childDataSnapshot.getValue().toString());
                }
                notes = titles.toArray(new String[0]);

                l = findViewById(R.id.listNotes);
                ArrayAdapter<String> arr;
                arr = new ArrayAdapter<String>(
                        ListView.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        notes);
                l.setAdapter(arr);
                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position,
                                            long id) {

                        Intent intent = new Intent(ListView.this, Note.class);
                        intent.putExtra("folder", key);
                        intent.putExtra("title",  notes[position]);
                        intent.putExtra("noteKey", notes[position]);
                        intent.putExtra("noteContent", noteContent.get(position));
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addButton = findViewById(R.id.addNoteButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListView.this);
                builder.setTitle("New Note");
                final EditText one = new EditText(ListView.this);
                one.setHint("Title");//optional
                one.setInputType(InputType.TYPE_CLASS_TEXT);
                LinearLayout lay = new LinearLayout(ListView.this);
                lay.setOrientation(LinearLayout.VERTICAL);
                lay.addView(one);
                builder.setView(lay);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        folder.child(one.getText().toString()).setValue("");
                Intent intent = new Intent(ListView.this, Note.class);
                intent.putExtra("title", one.getText().toString());
                startActivity(intent);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                builder.show();


            }
        });


        }
}