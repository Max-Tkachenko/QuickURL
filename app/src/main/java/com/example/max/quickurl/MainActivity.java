package com.example.max.quickurl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    List<Reference> references;
    MyAdapter adapter;
    SQLiteDatabase database;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        references = new ArrayList<>();

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        getDataFromDB();

        if (references.size() == 0) {
            setPopularLinks();
            getDataFromDB();
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("QuickURL");
            alert.setMessage("Add new links to your application!");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
        }

        adapter = new MyAdapter(this, references, dbHelper);
        ListView listView = (ListView) findViewById(R.id.listOfRef);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        adapter.setCheckBox(checkBox);
        adapter.setCtxFromMain(this);
        listView.setAdapter(adapter);
    }

    public void openDialog(final View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add new reference");

        LayoutInflater inflater = this.getLayoutInflater();

        final View viedForDialog = inflater.inflate(R.layout.dialog, null);
        alert.setView(viedForDialog);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText name = (EditText) viedForDialog.findViewById(R.id.editName);
                final EditText link = (EditText) viedForDialog.findViewById(R.id.editURL);
                if (name.getText().toString().length() == 0 || link.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter the data before you click \"Save\" !",
                            Toast.LENGTH_LONG);
                    dialog.cancel();
                    toast.show();
                } else if (link.getText().toString().contains("http")) {
                    Reference ref = new Reference(name.getText().toString(), link.getText().toString());
                    references.add(ref);
                    addURLtoSQLite(ref);
                    dialog.cancel();
                } else {
                    Reference ref = new Reference(name.getText().toString(), "http://" + link.getText().toString());
                    references.add(ref);
                    addURLtoSQLite(ref);
                    dialog.cancel();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void addURLtoSQLite(Reference ref) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME, ref.name);
        contentValues.put(DBHelper.KEY_LINK, ref.URL);
        database.insert(DBHelper.TABLE_URL, null, contentValues);
    }

    public void getDataFromDB() {
        Cursor cursor = database.query(DBHelper.TABLE_URL, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int linkIndex = cursor.getColumnIndex(DBHelper.KEY_LINK);
            do {
                references.add(new Reference(cursor.getString(nameIndex), cursor.getString(linkIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void setPopularLinks() {
        String[] names = { "Google", "Wikipedia", "Amazon" };
        String[] links = { "http://google.com", "http://wikipedia.com", "http://amazon.com" };
        for (int i = 0; i < 3; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_NAME, names[i]);
            contentValues.put(DBHelper.KEY_LINK, links[i]);
            database.insert(DBHelper.TABLE_URL, null, contentValues);
        }
    }

    public void goToInfoActivity(View view) {
        Intent toInfo = new Intent(this, InfoActivity.class);
        startActivity(toInfo);
    }
}
