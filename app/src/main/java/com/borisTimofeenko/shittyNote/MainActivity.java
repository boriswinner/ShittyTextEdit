package com.borisTimofeenko.shittyNote;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> notesListAdapter;
    private String[] notes;
    private ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPsychedelicFont();
        ImageView img = (ImageView)findViewById(R.id.swing_play);
        img.setImageResource(R.drawable.back_text_anim);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        AnimationDrawable backAnimation = (AnimationDrawable) img.getDrawable();
        backAnimation.start();


        notes = fileList();
        invertArray(notes);
        notesListView = (ListView) findViewById(R.id.notesList);
        // присваиваем адаптер списку
        notesListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, notes));
        notesListView.setClickable(true);
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                try{
                    InputStream fin = openFileInput(((TextView) arg1).getText().toString());
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fin.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    fin.close();
                    EditText text = (EditText) findViewById(R.id.inputEdit);
                    text.setText(result.toString("UTF-8"));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPsychedelicFont() {
        TextView textView = (TextView) findViewById(R.id.inputEdit);
        TextPaint paint = textView.getPaint();
        float width = paint.measureText("Tianjin, China");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),
                        Color.parseColor("#8446CC"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
    }

    public void saveToTxt(View view){
        EditText text = (EditText) findViewById(R.id.inputEdit);
        String fileContents = text.getText().toString();
        String fileName = fileContents.replaceAll("[\\\\/:*?\"<>|]", "_")
                .substring(0, Math.min(fileContents.length(), 10));
        try{
            FileOutputStream stream = openFileOutput(fileName, Context.MODE_PRIVATE);
            stream.write(fileContents.getBytes());
            stream.close();
            notes = fileList();
            invertArray(notes);
            notesListView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, notes));
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void newNote(View view){
        EditText text = (EditText) findViewById(R.id.inputEdit);
        text.setText("");
    }

    public void deleteNote(View view){
        EditText text = (EditText) findViewById(R.id.inputEdit);
        String fileContents = text.getText().toString();
        String fileName = fileContents.replaceAll("[\\\\/:*?\"<>|]", "_")
                .substring(0, Math.min(fileContents.length(), 10));
        deleteFile(fileName);
        notes = fileList();
        invertArray(notes);
        notesListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, notes));
        newNote(view);
        Toast.makeText(this, "Note Deleted!", Toast.LENGTH_SHORT).show();
    }

    private static void invertArray(String[] notes) {
        for(int i=0; i<notes.length/2; i++){
            String temp = notes[i];
            notes[i] = notes[notes.length -i -1];
            notes[notes.length -i -1] = temp;
        }
    }

}
