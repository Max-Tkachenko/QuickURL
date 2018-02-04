package com.example.max.quickurl;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater LInflater;
    List<Reference> objects;
    Button openBrowser;
    CheckBox checkBox;
    Context ctxFromMain;
    FloatingActionButton delete;
    TextView editItem;
    SQLiteDatabase database;

    MyAdapter(Context context, List<Reference> list, DBHelper dbHelper) {
        ctx = context;
        objects = list;
        LInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Reference getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LInflater.inflate(R.layout.item, parent, false);
        }

        Reference ref = getItem(position);

        ((TextView) view.findViewById(R.id.tvName)).setText(ref.name);
        ((TextView) view.findViewById(R.id.tvURL)).setText(ref.URL);

        openBrowser = (Button) view.findViewById(R.id.openURLbuton);
        openBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = getItem(position).URL;
                Log.d("Link to open", URL);
                Intent intent;

                if (checkBox.isChecked()) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                    parent.getContext().startActivity(intent);
                } else {
                    intent = new Intent(parent.getContext(), WebActivity.class);
                    intent.putExtra("URL", URL);
                    parent.getContext().startActivity(intent);
                }
            }
        });

        delete = (FloatingActionButton) view.findViewById(R.id.deleteItem);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctxFromMain);
                alert.setMessage("Are you sure to remove this reference?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reference ref = objects.get(position);
                        database.delete(DBHelper.TABLE_URL,DBHelper.KEY_NAME + "= ?", new String[] {ref.name});
                        objects.remove(position);
                        notifyDataSetChanged();
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
        });

        editItem = (TextView) view.findViewById(R.id.textEdit);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctxFromMain);
                alert.setTitle("Edit URL");
                final View viedForDialog = LInflater.inflate(R.layout.dialog, null);
                alert.setView(viedForDialog);

                final EditText name = (EditText) viedForDialog.findViewById(R.id.editName);
                final EditText link = (EditText) viedForDialog.findViewById(R.id.editURL);

                final String nameStr = getItem(position).name;
                final String linkStr = getItem(position).URL;

                name.setText(nameStr);
                link.setText(linkStr);

                name.setSelection(nameStr.length());
                link.setSelection(linkStr.length());

                alert.setPositiveButton("Save changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reference ref = objects.get(position);
                        String oldName = ref.name;
                        ref.name = name.getText().toString();
                        ref.URL = link.getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_NAME, ref.name);
                        contentValues.put(DBHelper.KEY_LINK, ref.URL);
                        database.update(DBHelper.TABLE_URL, contentValues, DBHelper.KEY_NAME + "= ?", new String[] {oldName});
                        objects.set(position, ref);
                        dialog.cancel();
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
        });

        return view;
    }

    public void setCheckBox(CheckBox box) {
        checkBox = box;
    }

    public void setCtxFromMain(Context context) {
        ctxFromMain = context;
    }
}
