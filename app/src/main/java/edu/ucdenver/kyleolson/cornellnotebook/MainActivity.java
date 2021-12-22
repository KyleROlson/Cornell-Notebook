package edu.ucdenver.kyleolson.cornellnotebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ListView l;
    String uid;
    String tutorials[]
            = {"Unit Circle", "Picasso", "Sin, Cos, and Tan", "Chemical Reactions"};

    private DatabaseReference mDatabase;
    private DatabaseReference userFolder;
    private ArrayList<Notebook> notebooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("uid");
        }


        l = findViewById(R.id.recentNotes);
        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                tutorials);
        l.setAdapter(arr);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userFolder = mDatabase.child(uid);

        DatabaseReference databaseReference = userFolder;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout notebookContainer = findViewById(R.id.notebookContainer);
                notebooks.clear();
                notebookContainer.removeAllViews();
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Notebook notebook;
                    notebook = postSnapshot.getValue(Notebook.class);
                    notebook.key = postSnapshot.getKey();
                    notebooks.add(notebook);
                }
                for(int i = 0; i<notebooks.size(); i++){
                    com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(MainActivity.this);
                    float width =  160 * MainActivity.this.getResources().getDisplayMetrics().density;
                    float height =  240 * MainActivity.this.getResources().getDisplayMetrics().density;
                    float spacing =  10 * MainActivity.this.getResources().getDisplayMetrics().density;
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) width,(int) height);
                    card.setContentPadding(0,0,(int)spacing,0);
                    card.setClickable(true);
                    String name = notebooks.get(i).name;
                    String key = notebooks.get(i).key;

                    card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, edu.ucdenver.kyleolson.cornellnotebook.ListView.class);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                intent.putExtra("key", key);
                startActivity(intent);

            }
        });
                    card.setLayoutParams(params);
                    ImageView iv = new ImageView(MainActivity.this);
                   Picasso.get().load(notebooks.get(i).pathToBackground).fit().into(iv);
                    card.addView(iv);
                    notebookContainer.addView(card);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        userFolder = mDatabase.child("kyle").child("1").child("notes");
//        userFolder.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
//                    //displays the key for the node
//                    //Log.v(TAG,""+ childDataSnapshot.child(--ENTER THE KEY NAME eg. firstname or email etc.--).getValue());   //gives the value for given keyname
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void notebookAdd(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //you should edit this to fit your needs
        builder.setTitle("Create Notebook");

        final EditText one = new EditText(this);
        one.setHint("Notebook Name");//optional
        final EditText two = new EditText(this);
        two.setHint("Link to Cover Photo");//optional

        one.setInputType(InputType.TYPE_CLASS_TEXT);
        two.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(one);
        lay.addView(two);
        builder.setView(lay);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Notebook notebook = new Notebook();
                notebook.name = one.getText().toString();
                notebook.pathToBackground = two.getText().toString();
                DatabaseReference newRef = userFolder.push();
                newRef.setValue(notebook);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}
