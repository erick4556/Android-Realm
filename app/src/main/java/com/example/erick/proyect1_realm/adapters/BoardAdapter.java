package com.example.erick.proyect1_realm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.erick.proyect1_realm.R;
import com.example.erick.proyect1_realm.models.Board;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class BoardAdapter extends BaseAdapter {

    private Context context;
    private List<Board> list;
    private int layout;

    public BoardAdapter(Context context, List<Board> boards, int layout){
        this.context = context;
        this.list = boards;
        this.layout = layout;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Board getItem(int position) {
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
            convertview = LayoutInflater.from(context).inflate(layout,null); //se infla la vista
            vh = new ViewHolder();
            vh.title = (TextView)convertview.findViewById(R.id.textViewBoardTitle);
            vh.notes = (TextView)convertview.findViewById(R.id.textViewBoardNotes);
            vh.createdAt = (TextView)convertview.findViewById(R.id.textViewBoardDate);

            convertview.setTag(vh);
        }else{
            vh = (ViewHolder)convertview.getTag();
        }

        Board board = list.get(position);
        vh.title.setText(board.getTitle());

        int numberofNotes = board.getNotes().size();
        String textForNotes = (numberofNotes == 1)? numberofNotes + " Note" : numberofNotes + " Notes";
        vh.notes.setText(textForNotes);

        //formateo de la fecha
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String createdAt_format = df.format(board.getCreadedAt());

        vh.createdAt.setText(createdAt_format);

        return convertview;
    }


    public class ViewHolder{
        TextView title;
        TextView notes;
        TextView createdAt;
    }

}
