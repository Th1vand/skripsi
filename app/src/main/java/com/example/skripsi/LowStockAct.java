package com.example.skripsi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LowStockAct extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter adapter;
    private ArrayList<UserItems_home> dataModelList;
    FirebaseAuth auth;
    FirebaseUser user;
    int number;
    ImageButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.low_stock_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        home = findViewById(R.id.Homebtn);

        dataModelList = new ArrayList<>();
        adapter = new ItemRecyclerViewAdapter(LowStockAct.this,dataModelList);
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stock"+"-"+user.getUid());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItems_home dataModel = snapshot.getValue(UserItems_home.class);
                    number = Integer.parseInt(dataModel.getCount());
                    if(number <5){
                        dataModelList.add(dataModel);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
        home.setOnClickListener(view->{
            Intent intent = new Intent(LowStockAct.this, Main.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}