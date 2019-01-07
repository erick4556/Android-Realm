package com.example.erick.proyect1_realm.activities;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.erick.proyect1_realm.adapters.BoardAdapter;
import com.example.erick.proyect1_realm.models.Board;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>>, AdapterView.OnItemClickListener {

    private Realm realm;

    FloatingActionButton fab;

    private ListView listView;

    private BoardAdapter adapter;

    private RealmResults<Board> boards; //Lista de nuestro modelo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Db Realm
        realm = Realm.getDefaultInstance();
        boards = realm.where(Board.class).findAll(); //consulta
        boards.addChangeListener(this);

        adapter = new BoardAdapter(this, boards, R.layout.list_view_board_item);

        listView = (ListView)findViewById(R.id.ListViewBoard);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton)findViewById(R.id.fabAddBoard);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForBoard("Agrega nuevo tablero", "Escribe un nombre para el tablero");
            }
        });

        //Para el context menu, registrarlo
        registerForContextMenu(listView);

    }

    //CRUD Actions

    private void createNewBoard(String boardName) {
        realm.beginTransaction();
        Board board = new Board(boardName);
        realm.copyToRealm(board);
        realm.commitTransaction();

        //otra forma
       /* realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Board board = new Board(boardName);
                realm.copyToRealm(board);
            }
        });*/

    }

    private void deleteboard(Board board){
        realm.beginTransaction();
        board.deleteFromRealm();//Se borra de la base de datos
        realm.commitTransaction();
    }

    private void editBoard(String newName, Board board){
        realm.beginTransaction();
        board.setTitle(newName);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }

    private void deleteAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    //Dialogs
    private void showAlertForBoard(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title!= null){
            builder.setTitle(title);
        }

        if(message !=null){
            builder.setMessage(message);
        }

        View viewInflated =  LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null); //Se va inflar el dialogo

        builder.setView(viewInflated); //Metemos la vista inflada al dialogo

        final EditText input = (EditText) viewInflated.findViewById(R.id.edittextBoard);

        //Configuración de la acción del boton

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String boardName = input.getText().toString().trim();   //trim() para borrar espacios si hay al principio o final
                if(boardName.length() > 0){
                    createNewBoard(boardName);
                }else{
                    Toast.makeText(getApplicationContext(),"El nombre es requerido para crear un nuevo Board",Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingBoard(String title, String message, final Board board){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title!= null){
            builder.setTitle(title);
        }

        if(message !=null){
            builder.setMessage(message);
        }

        View viewInflated =  LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null); //Se va inflar el dialogo

        builder.setView(viewInflated); //Metemos la vista inflada al dialogo

        final EditText input = (EditText) viewInflated.findViewById(R.id.edittextBoard);
        input.setText(board.getTitle());//Texto por defecto, actual título del tablero se va enseñar por defecto

        //Configuración de la acción del boton

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String boardName = input.getText().toString().trim();   //trim() para borrar espacios si hay al principio o final
                if(boardName.length() == 0){
                    Toast.makeText(getApplicationContext(),"El nombre es requerido para editar un nuevo Board",Toast.LENGTH_SHORT).show();

                }else
                    if(boardName.equals(board.getTitle())){//Para que no sea el mismo nombre
                        Toast.makeText(getApplicationContext(),"El nombre es el mismo que estaba antes",Toast.LENGTH_SHORT).show();
                    }else{
                        editBoard(boardName, board);
                    }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*Events*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteall:
                deleteAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Editar o borrar cada uno de los elementos
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //Nombre de la nota a borrar
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_board_activity,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_board:
                deleteboard(boards.get(info.position));
                return true;

            case R.id.edit_board:
                showAlertForEditingBoard("Edit Board","Cambia el nombre del tablero",boards.get(info.position));//se pasa como parámetro el titulo y el mensaje de esa alerta y el board que se ha seleccionado
                return true;

             default:
                 return super.onContextItemSelected(item);
        }


    }

    @Override
    public void onChange(RealmResults<Board> boards) {
         adapter.notifyDataSetChanged(); //Refrescador del adaptador
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("id",boards.get(position).getId());
        startActivity(intent);

    }
}
