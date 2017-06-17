package com.hrily.zbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hrily.zbot.adapters.GameListAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.game_list)
    RecyclerView recyclerView;

    GameListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new GameListAdapter(this,
                Arrays.asList(new String[]{"Acheton", "Advent", "ZDungeon"}),
                Arrays.asList(new String[]{"Acheton.z8", "advent.z3", "zdungeon.z5"}));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
