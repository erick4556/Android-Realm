package com.example.erick.proyect1_realm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.erick.proyect1_realm.R;
import com.example.erick.proyect1_realm.models.Board;
import com.example.erick.proyect1_realm.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoteAdapter extends BaseAdapter {


    private Context context;
    private List<Note> list;
    private int layout;

    public NoteAdapter(Context context, List<Note> notes, int layout){
        this.context = context;
        this.list = notes;
        this.layout = layout;
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {

        ViewHolder vh;

        if(convertview == null){
            convertview = LayoutInflater.from(context).inflate(layout,null);
            vh = new ViewHolder();
            vh.description = (TextView)convertview.findViewById(R.id.textViewNoteDescription);
            vh.createdAt = (TextView)convertview.findViewById(R.id.textViewNoteCreatedAt);
            convertview.setTag(vh);
        }else{
            vh = (ViewHolder) convertview.getTag();
        }

        Note note  = list.get(position); //position cada vez viene con un valor diferente

        vh.description.setText(note.getDescription());
        //Formateo de fecha
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(note.getCreadedAt());
        vh.createdAt.setText(date);

        return convertview;

    }

    public class ViewHolder{
        TextView description;
        TextView createdAt;
    }

}
