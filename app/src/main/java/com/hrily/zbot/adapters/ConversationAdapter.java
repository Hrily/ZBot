package com.hrily.zbot.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hrily.zbot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by hrishi on 17/6/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    Context context;
    public List< Pair<String, Boolean> > conversation;

    public ConversationAdapter(Context context) {
        this.context = context;
        this.conversation = new ArrayList<>();
    }

    public ConversationAdapter(Context context, List<Pair<String, Boolean>> conversation) {
        this.context = context;
        this.conversation = conversation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new LinearLayout(context);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(rlp);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setChip(conversation.get(position));
    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            linearLayout = (LinearLayout) view;
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        }

        public void setChip(Pair<String, Boolean> chip){
            String message = chip.first;
            Boolean isCPU = chip.second;
            linearLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View chipView;
            if(isCPU)
                chipView = inflater.inflate(R.layout.chip_cpu, null);
            else
                chipView = inflater.inflate(R.layout.chip_user, null);
            TextView textView = ButterKnife.findById(chipView, R.id.text);
            textView.setText(message);
            linearLayout.addView(chipView);
        }

    }

}
