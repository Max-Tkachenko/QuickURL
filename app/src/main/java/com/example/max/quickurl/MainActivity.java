package com.example.max.quickurl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends Activity {

    ArrayList<Reference> list = new ArrayList<Reference>();
    MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        list = getListFromString(readFromMemory());

        if(list.size() == 0){
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

        adapter = new MyAdapter(this, list);
        ListView listView = (ListView) findViewById(R.id.listOfRef);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        adapter.setCheckBox(checkBox);
        adapter.setCtxFromMain(this);
        listView.setAdapter(adapter);
    }

    public void openInfoActivity(View view){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void openDialog(final View view){
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
                    if(name.getText().toString().length() == 0 || link.getText().toString().length() == 0){
                        Toast toast = Toast.makeText(getApplicationContext(), "Enter the data before you click \"Save\" !",
                                Toast.LENGTH_LONG);
                        dialog.cancel();
                        toast.show();
                    }
                    else if(link.getText().toString().contains("http")){
                        list.add(new Reference(name.getText().toString(), link.getText().toString()));
                        writeToMemory(list);
                        dialog.cancel();
                    }
                    else {
                        list.add(new Reference(name.getText().toString(), "http://"+link.getText().toString()));
                        writeToMemory(list);
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

    public String readFromMemory(){
        String fromMemory = "";
        try {
            FileInputStream fIn = openFileInput("infoURL.txt");
            InputStreamReader isr = new InputStreamReader(fIn);
            int i = -1;
            while ((i=fIn.read()) != -1){
                fromMemory += (char)i;
            }
        }
        catch (Resources.NotFoundException ex) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return fromMemory;
    }

    public void writeToMemory(ArrayList<Reference> list){
        String str = "";
        for (int i = 0; i < list.size(); i++){
            str += list.get(i).name + " ";
            str += list.get(i).URL + " ";
        }

        try {
            FileOutputStream fOut = openFileOutput("infoURL.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(str);
            osw.flush();
            osw.close();
        }
        catch (Resources.NotFoundException ex) {
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Reference> getListFromString(String str){
        ArrayList<Reference> URL_list = new ArrayList<>();
        if(str.length() > 0) {
            String[] array = str.split(" ");

            ArrayList<String> names = new ArrayList<>(array.length / 2);
            ArrayList<String> references = new ArrayList<>(array.length / 2);
            for (int i = 0; i < array.length; i++) {
                if (i % 2 == 0) {
                    names.add(array[i]);
                } else {
                    references.add(array[i]);
                }
            }

            for (int i = 0; i < names.size(); i++) {
                URL_list.add(new Reference(names.get(i), references.get(i)));
            }
        }
        return URL_list;
    }
}
