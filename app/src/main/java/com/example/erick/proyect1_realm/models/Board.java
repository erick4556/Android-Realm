package com.example.erick.proyect1_realm.models;

import com.example.erick.proyect1_realm.app.MyApplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Board extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String title;

    @Required
    private Date creadedAt;

    //La relación entre el tablero y las notas
    private RealmList<Note> notes;

    //Realm requeire de un constructor vacio
    public Board(){

    }

    public Board(String title){
        this.id = MyApplication.BoardID.incrementAndGet(); //Incrementar y obtener
        this.title = title;
        this.notes = new RealmList<Note>();
        this.creadedAt =  new Date();
    }

    public int getId() {
        return id;
    }

    //Set de id no se requiere por que va ser un id autogenerado

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreadedAt() {
        return creadedAt;
    }


    public RealmList<Note> getNotes() {
        return notes;
    }

    //Set de notes no se va utilizar por que no se va dar una lista entera

}
