package com.example.max.quickurl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater LInflater;
    ArrayList<Reference> objects;
    Button openBrowser;
    CheckBox checkBox;
    Context ctxFromMain;
    FloatingActionButton delete;
    TextView editItem;

    MyAdapter(Context context, ArrayList<Reference> list) {
        ctx = context;
        objects = list;
        LInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        objects.remove(position);
                        notifyDataSetChanged();

                        String str = "";
                        for (int i = 0; i < objects.size(); i++) {
                            str += objects.get(i).name + " ";
                            str += objects.get(i).URL + " ";
                        }
                        try {
                            FileOutputStream fOut = parent.getContext().openFileOutput("infoURL.txt", MODE_PRIVATE);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write(str);
                            osw.flush();
                            osw.close();
                        }
                        catch (Resources.NotFoundException ex) { }
                        catch (IOException e) {
                            e.printStackTrace();
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
                        String nameAfter = name.getText().toString();
                        String linkAfter = link.getText().toString();
                        objects.set(position, new Reference(nameAfter, linkAfter));
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
