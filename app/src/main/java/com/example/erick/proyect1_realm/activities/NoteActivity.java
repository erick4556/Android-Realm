package com.example.erick.proyect1_realm.activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.erick.proyect1_realm.R;
import com.example.erick.proyect1_realm.adapters.NoteAdapter;
import com.example.erick.proyect1_realm.models.Board;
import com.example.erick.proyect1_realm.models.Note;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board> {

    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter adapter;
    private RealmList<Note> notes; //para poder hacer la relación
    private Realm realm;


    private int boardId; //El id que viene del board, para recuperar el objeto completo
    private Board board; //traigo el objeto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        realm = Realm.getDefaultInstance();

        if(getIntent().getExtras()!=null){
            boardId = getIntent().getExtras().getInt("id");
        }

        board = realm.where(Board.class).equalTo("id",boardId).findFirst();//Se recupera el tablero da la base de datos. Si no fuese el id, sino color seria findAll()
        board.addChangeListener(this);
        notes = board.getNotes();//Se obtienen todas las notas

        //Cambiar titulo de la activity por el nombre de la pizarra
        this.setTitle(board.getTitle());

        fab = (FloatingActionButton)findViewById(R.id.fabAddNote);
        listView = (ListView)findViewById(R.id.ListViewNote);
        adapter = new NoteAdapter(this,notes, R.layout.list_view_note_item);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForNote("Add new note","Type a note for "+board.getTitle() + ".");
            }
        });

        //Para el context menu, registrarlo
        registerForContextMenu(listView);
    }

    //CRUD Actions

    private void createNewNote(String note){
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        //La relación
        board.getNotes().add(_note);
        realm.commitTransaction();
    }


    private void editNote(String newNoteDescription, Note note){
        realm.beginTransaction();
        note.setDescription(newNoteDescription);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    private void deleteNote(Note note){
        realm.beginTransaction();
        note.deleteFromRealm();//Se borra de la base de datos
        realm.commitTransaction();
    }

    private void deleteAll(){
        //Se va borrar todas las notas de ese tablero
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm(); //Se borran todas las notas de ese tablero
        realm.commitTransaction();
    }

    //Dialogs
    private void showAlertForNote(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title!= null){
            builder.setTitle(title);
        }

        if(message !=null){
            builder.setMessage(message);
        }

        View viewInflated =  LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);//Se va inflar el dialogo

        builder.setView(viewInflated); //Metemos la vista inflada al dialogo

        final EditText input = (EditText) viewInflated.findViewById(R.id.edittextNote); //Se captura el edittext de la vista inflada

        //Configuración de la acción del boton

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String note = input.getText().toString().trim();   //trim() para borrar espacios si hay al principio o final
                if(note.length() > 0){
                    createNewNote(note);
                }else{
                    Toast.makeText(getApplicationContext(),"La nota no puede estar vacía",Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingNote(String title, String message, final Note note){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title!= null){
            builder.setTitle(title);
        }

        if(message !=null){
            builder.setMessage(message);
        }

        View viewInflated =  LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);//Se va inflar el dialogo

        builder.setView(viewInflated); //Metemos la vista inflada al dialogo

        final EditText input = (EditText) viewInflated.findViewById(R.id.edittextNote); //Se captura el edittext de la vista inflada
        input.setText(note.getDescription());//Texto por defecto, actual descripción de la tablero se va enseñar por defecto

        //Configuración de la acción del boton

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String noteDescription = input.getText().toString().trim();   //trim() para borrar espacios si hay al principio o final
                if(noteDescription.length() == 0){
                    Toast.makeText(getApplicationContext(),"La nota es requerida para editar una nueva nota",Toast.LENGTH_SHORT).show();

                }else
                if(noteDescription.equals(note.getDescription())){//Para que no sea el mismo nombre
                    Toast.makeText(getApplicationContext(),"La nota es la misma que estaba antes",Toast.LENGTH_SHORT).show();
                }else{
                    editNote(noteDescription, note);
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteall_notes:
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //No va título por que la descripción puede ser larga
        getMenuInflater().inflate(R.menu.context_menu_note_activity,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_note:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.edit_note:
                showAlertForEditingNote("Editar note","Cambiar la nota", notes.get(info.position));
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }
}
