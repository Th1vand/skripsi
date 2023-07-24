package com.example.skripsi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;



public class All_item extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemRecyclerViewAdapter adapter;
    private ArrayList<UserItems_home> dataModelList;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageButton home,historyBtn;
    ImageView bg;
    EditText searchText;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_item_layout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recViewAllItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        home = findViewById(R.id.Homebtn);
        searchText = findViewById(R.id.searchfieldallpage);
        historyBtn = findViewById(R.id.historyBtn);
        bg = findViewById(R.id.imageView);

        dataModelList = new ArrayList<>();
        adapter = new ItemRecyclerViewAdapter(All_item.this,dataModelList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stock"+"-"+user.getUid());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItems_home dataModel = snapshot.getValue(UserItems_home.class);
                    dataModelList.add(dataModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });

//        CustomItemTouchListener itemTouchListener = new CustomItemTouchListener();
//        recyclerView.addOnItemTouchListener(itemTouchListener);

//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                View childView = recyclerView.findChildViewUnder(e.getX(0), e.getY(0));
//                if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
//                    int position = recyclerView.getChildAdapterPosition(childView);
//                    UserItems_home clickedItem = dataModelList.get(position);
//
//                    ViewItemDetail viewItemDetail = new ViewItemDetail();
//                    viewItemDetail.showDialog(All_item.this,clickedItem.getID(),clickedItem.getProductCode(),clickedItem.getName(),clickedItem.getPrice(),clickedItem.getCount(),clickedItem.getIn(),clickedItem.getExpired());
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });



//        searchText.setOnFocusChangeListener((v, hasFocus) -> {
//            if(hasFocus){
//                home.setVisibility(View.INVISIBLE);
//                historyBtn.setVisibility(View.INVISIBLE);
//                bg.setVisibility(View.INVISIBLE);
//            }else{
//                home.setVisibility(View.VISIBLE);
//                historyBtn.setVisibility(View.VISIBLE);
//                bg.setVisibility(View.VISIBLE);
//            }
//
//        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()!=null){
                    readData(s.toString());
                }else{
                    readData("");
                }
            }
        });

        home.setOnClickListener(view->{
            Intent intent = new Intent(All_item.this, Main.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        historyBtn.setOnClickListener(view->{
            Intent intent = new Intent(All_item.this, historyPage.class);
            startActivity(intent);
        });

    }

//    public class CustomItemTouchListener implements RecyclerView.OnItemTouchListener {
//        private static final int TOUCH_SENSITIVITY_THRESHOLD = 2; // Adjust this value as needed
//        private boolean isTouchIntercepted = false;
//        private float startX, startY;
//
//
//        @Override
//        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//            View childView = recyclerView.findChildViewUnder(e.getX(0), e.getY(0));
//            switch (e.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startX = e.getX();
//                    startY = e.getY();
//                    isTouchIntercepted = false;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float dx = Math.abs(e.getX() - startX);
//                    float dy = Math.abs(e.getY() - startY);
//                    if (dx > TOUCH_SENSITIVITY_THRESHOLD || dy > TOUCH_SENSITIVITY_THRESHOLD) {
//                        isTouchIntercepted = true;
//                    }
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_UP:
//                    startX = 0;
//                    startY = 0;
//                    int position = recyclerView.getChildAdapterPosition(childView);
//                    UserItems_home clickedItem = dataModelList.get(position);
//
//                    ViewItemDetail viewItemDetail = new ViewItemDetail();
//                    viewItemDetail.showDialog(All_item.this,clickedItem.getID(),clickedItem.getProductCode(),clickedItem.getName(),clickedItem.getPrice(),clickedItem.getCount(),clickedItem.getIn(),clickedItem.getExpired());
//                    break;
//            }
//
//            return isTouchIntercepted;
//        }
//
//        @Override
//        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//            // Handle touch events here if needed
//        }
//
//        @Override
//        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//            // Handle request to disallow touch event interception if needed
//        }
//    }
    private void readData(String data) {

        databaseReference.child("stock"+"-"+user.getUid()).orderByChild("name").startAt(data).endAt(data+"\uf8ff").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot) {
                dataModelList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
                    dataModelList.add(users);
                }
                adapter = new ItemRecyclerViewAdapter(All_item.this,dataModelList);

                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error) {

            }
        });
    }

    public class ViewItemDetail{
        public void showDialog(Context context, String id,String ProductCode, String Name,String Price, String Quantity, String DateIn, String ExpDate ) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.user_items_detail);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            TextView productCode = dialog.findViewById(R.id.detProductCode);
            TextView itemName = dialog.findViewById(R.id.detItemName);
            TextView itemPrice = dialog.findViewById(R.id.detPrice);
            TextView itemQty = dialog.findViewById(R.id.detQuantity);
            TextView itemDateIn = dialog.findViewById(R.id.detDateIn);
            TextView itemExpDate = dialog.findViewById(R.id.detDateExpired);

            productCode.setText(ProductCode);
            itemName.setText(Name);
            itemPrice.setText("Rp."+" "+Price);
            itemQty.setText(Quantity);
            itemDateIn.setText(DateIn);
            itemExpDate.setText(ExpDate);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setCanceledOnTouchOutside(true);
            dialog.onBackPressed();
            dialog.show();

        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
