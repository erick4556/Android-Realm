package com.example.erick.proyect1_realm.app;

import android.app.Application;

import com.example.erick.proyect1_realm.models.Board;
import com.example.erick.proyect1_realm.models.Note;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    public static AtomicInteger BoardID = new AtomicInteger();
    public static AtomicInteger NoteId =  new AtomicInteger();

    //Esta clase siempre sera ejecutada antes del mainactivity

    @Override
    public void onCreate() {
        //Leer de la base de datos cual es el máximo id que tenemos
        super.onCreate();

        //La configuración
        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance(); //Me de una instancia por defecto
        BoardID = getIdByTable(realm, Board.class);
        NoteId = getIdByTable(realm, Note.class);

        //Evitar la pérdida de conexión
        RealmInspectorModulesProvider realmInspector = RealmInspectorModulesProvider.builder(this)
                .withDeleteIfMigrationNeeded(true)
                .build();

        //Implementación de setho
        //Se inicializa la libreria
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(realmInspector)
                        .build());

        realm.close();

    }

    //Configuración por defecto de la base de datos realm para el contexto de la aplicación
    private void setUpRealmConfig(){

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

    }


    //Extiende de realm, devuelve un atomicinteger, recibe la base datos realm y cualquier clase.
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){

        //Consulta, que encuentre todo de esa tabla
        RealmResults<T> results = realm.where(anyClass).findAll();

        //Misma forma que la de abajo
       /* if(results.size() > 0 ){
            new AtomicInteger(results.max("id").intValue()); //Dame el máximo id
        }else{
            new AtomicInteger();
        }*/

        return (results.size() > 0)? new AtomicInteger(results.max("id").intValue()): new AtomicInteger();

    }

}
