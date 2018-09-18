package com.caspater.notepad;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.caspater.notepad.data.NoteItem;
import com.caspater.notepad.data.NotesDataSource;

import java.util.List;

public class MainActivity extends ListActivity {
    public static final int REQUEST_CODE = 1000;
    private static final int MENU_DEL=102;
    private int currentNoteId;
    private NotesDataSource datasource;
    List<NoteItem> notesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        datasource=new NotesDataSource(this);
        
        refreshDisplay();

    }

    private void refreshDisplay() {
        notesList=datasource.findAll();
        ArrayAdapter<NoteItem> adapter=new ArrayAdapter<NoteItem>(this, R.layout.list_item_layout,notesList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            createNote();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNote() {
        NoteItem note=NoteItem.getNew();
        Intent intent=new Intent(this,NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("Text", note.getText());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        NoteItem note=notesList.get(position);
        Intent intent=new Intent(this,NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("Text", note.getText());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            NoteItem note=new NoteItem();
            note.setKey(data.getStringExtra("key"));
            note.setText(data.getStringExtra("Text"));
            datasource.update(note) ;
            refreshDisplay();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) menuInfo;
        currentNoteId= (int) info.id;
        menu.add(0,MENU_DEL,0,"Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==MENU_DEL){
            NoteItem note =notesList.get(currentNoteId);
            datasource.remove(note);
            refreshDisplay();
        }
        return super.onContextItemSelected(item);
    }
}
