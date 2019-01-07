package com.example.erick.proyect1_realm.models;

import com.example.erick.proyect1_realm.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Note extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String description;

    @Required
    private Date creadedAt;

    public Note(){

    }

    public Note(String  description){
        this.id = MyApplication.NoteId.incrementAndGet(); //Incrementar y obtener
        this.description = description;
        this.creadedAt = new Date();
    }

    public int getId() {
        return id;
    }

    //Set de id no se requiere por que va ser un id autogenerado

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreadedAt() {
        return creadedAt;
    }

    //Tampoco por que el date por que no se va manipular
}
