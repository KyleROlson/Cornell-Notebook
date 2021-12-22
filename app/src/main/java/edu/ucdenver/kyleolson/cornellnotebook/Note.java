package edu.ucdenver.kyleolson.cornellnotebook;

import static xute.markdeditor.Styles.TextComponentStyle.BLOCKQUOTE;
import static xute.markdeditor.Styles.TextComponentStyle.H1;
import static xute.markdeditor.Styles.TextComponentStyle.H5;
import static xute.markdeditor.Styles.TextComponentStyle.NORMAL;
import static xute.markdeditor.components.TextComponentItem.MODE_PLAIN;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

import xute.markdeditor.EditorControlBar;
import xute.markdeditor.MarkDEditor;
import xute.markdeditor.datatype.DraftDataItemModel;
import xute.markdeditor.models.DraftModel;
import xute.markdeditor.utilities.FilePathUtils;


public class Note extends AppCompatActivity implements EditorControlBar.EditorControlListener {
    private MarkDEditor markDEditor;
    private EditorControlBar editorControlBar;
    private final int REQUEST_IMAGE_SELECTOR = 110;
    private String linkTextString = "";
    private String linkString = "";
    private String title;
    private PopupMenu mPopupMenu;
    private DatabaseReference folder;
    String uid;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();

            MarkDEditor markDEditor = findViewById(R.id.mdEditor);
            editorControlBar = findViewById(R.id.controlBar);
            markDEditor.configureEditor(
                    "https://api.hapramp.com/api/v2/",
                    "",
                    true,
                    "Start Here...",
                    BLOCKQUOTE
            );

        if (extras != null) {
            String noteFolder = extras.getString("folder");
            String noteKey = extras.getString("noteKey");
            String noteContent = extras.getString("noteContent");
            uid = extras.getString("uid");
            Log.i("note content", noteContent);
            Log.i("note key:", noteKey);
            title = extras.getString("title");
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            folder = mDatabase.child(uid).child(noteFolder).child("notes").child(noteKey);
            if (!noteContent.equals("")){
                Gson gson = new Gson();
                DraftModel dm= gson.fromJson(noteContent, DraftModel.class);
                markDEditor.loadDraft(dm);
            }
            else{
                markDEditor.loadDraft(getDraftContent());
            }

        }

            //markDEditor.loadDraft(getDraftContent());
            editorControlBar = findViewById(R.id.controlBar);
            editorControlBar.setEditorControlListener(this);
            editorControlBar.setEditor(markDEditor);

//            DraftModel dm = markDEditor.getDraft();
//        String editorJson = new Gson().toJson(dm);
//        Log.i("json", editorJson);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonOption);
       PopupMenu dropDownMenu = new PopupMenu(Note.this, imageButton);
        Menu menu = dropDownMenu.getMenu();
// add your items:
        menu.add(0, 0, 0, "Save");
        menu.add(0, 1, 0, "Quit");

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        DraftModel dm = markDEditor.getDraft();
                        String editorJson = new Gson().toJson(dm);
                        folder.setValue(editorJson);
                        Toast.makeText(Note.this, "Note saved.",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case 1:
//                        Intent intent = new Intent(Note.this, MainActivity.class);
//                        startActivity(intent);
                        finish();
                        return true;
                }
                return false;
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });


    }
    private void openGallery() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_SELECTOR);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_IMAGE_SELECTOR);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_IMAGE_SELECTOR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                }
                break;
        }
    }

    @Override
    public void onInsertImageClicked() {
        openGallery();
    }

    @Override
    public void onInserLinkClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //you should edit this to fit your needs
        builder.setTitle("Double Edit Text");

        final EditText one = new EditText(this);
        one.setHint("Text To Display");//optional
        final EditText two = new EditText(this);
        two.setHint("Link");//optional

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
                //markDEditor.addLink("Click Here", "http://www.hapramp.com");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        builder.show();


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECTOR) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String filePath = FilePathUtils.getPath(this, uri);
                addImage(filePath);
            }
        }
    }
    public void addImage(String filePath) {
        markDEditor.insertImage(filePath);
    }

    private DraftModel getDraftContent() {
        ArrayList<DraftDataItemModel> contentTypes = new ArrayList<>();
        DraftDataItemModel heading = new DraftDataItemModel();
        heading.setItemType(DraftModel.ITEM_TYPE_TEXT);
        heading.setContent(title);
        heading.setMode(MODE_PLAIN);
        heading.setStyle(H1);

        DraftDataItemModel sub_heading = new DraftDataItemModel();
        sub_heading.setItemType(DraftModel.ITEM_TYPE_TEXT);
        sub_heading.setContent("Essential Question: ");
        sub_heading.setMode(MODE_PLAIN);
        sub_heading.setStyle(H5);

        DraftDataItemModel hrType = new DraftDataItemModel();
        hrType.setItemType(DraftModel.ITEM_TYPE_HR);

        DraftDataItemModel body = new DraftDataItemModel();
        body.setItemType(DraftModel.ITEM_TYPE_TEXT);
        body.setContent("Notes:\n");
        body.setMode(MODE_PLAIN);
        body.setStyle(NORMAL);

        contentTypes.add(heading);
        contentTypes.add(sub_heading);
        contentTypes.add(hrType);
        contentTypes.add(body);

        return new DraftModel(contentTypes);
    }

}