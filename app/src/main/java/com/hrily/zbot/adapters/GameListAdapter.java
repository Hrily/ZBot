package com.hrily.zbot.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hrily.zbot.GameActivity;
import com.hrily.zbot.R;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by hrishi on 16/6/17.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {


    Context context;
    List<String> games;
    List<String> gameFiles;

    public GameListAdapter(Context context, List<String> games, List<String> gameFiles) {
        this.context = context;
        this.games = games;
        this.gameFiles = gameFiles;
    }

    @Override
    public GameListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_list_item, parent, false);
        return new GameListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(games.get(position));
        holder.setOnClickListener(position);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = ButterKnife.findById(itemView, R.id.text);
        }

        public void setOnClickListener(final int position){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameActivity.class);
                    intent.putExtra("title", gameFiles.get(position));
                    context.startActivity(intent);
                }
            });
        }

    }
}
