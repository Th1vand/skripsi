package com.example.skripsi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class historyPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private historyRecviewAdapter adapter;
    private ArrayList<UserItems_home> dataModelList;

    List<UserItems_home> datalist;

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageButton home;
    TextView startDate;
    String currentDate;
    int year,month,day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.historyRecView);
        home = findViewById(R.id.Homebtn1);
        startDate = findViewById(R.id.startDate);
        //endDate = findViewById(R.id.EndDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataModelList = new ArrayList<>();
        adapter = new historyRecviewAdapter(historyPage.this,dataModelList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
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

        startDate.setOnClickListener(v -> {

            calendar();

            DatePickerDialog dialog1;
            dialog1 = new DatePickerDialog(historyPage.this, (view, year1, month1, dayOfMonth) -> {
                year = year1;
                month = month1;
                day = dayOfMonth;

                startDate.setText(day+"/"+month+"/"+year);
            },year,month,day);
            dialog1.show();
        });

//        endDate.setOnClickListener(v -> {
//
//            calendar();
//            DatePickerDialog dialog1;
//            dialog1 = new DatePickerDialog(historyPage.this, (view, year1, month1, dayOfMonth) -> {
//                year = year1;
//                month = month1;
//                day = dayOfMonth;
//
//                endDate.setText(day+"/"+month+"/"+year);
//            },year,month,day);
//            dialog1.show();
//        });


        calendar();
        currentDate =day+"/"+month+"/"+year;

        home.setOnClickListener(view->{
            Intent intent = new Intent(historyPage.this, Main.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        startDate.addTextChangedListener(new TextWatcher() {
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
                    readData(currentDate);
                }
            }
        });

//        endDate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString()!=null){
//                    readData(currentDate.toString(),s.toString());
//                }else{
//                    readData("","");
//                }
//            }
//        });
//

    }

    public void calendar(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    private void readData(String date) {
        databaseReference = db.getReference("in"+"-"+user.getUid());
        databaseReference1 = db.getReference("out"+"-"+user.getUid());

        Query q1 = databaseReference.orderByChild("in").equalTo(date);
        Query q2 = databaseReference1.orderByChild("in").equalTo(date);

          dataModelList.clear();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
                            dataModelList.add(users);

                        }
                        Collections.reverse(dataModelList);
                        Collections.sort(dataModelList, new Comparator<UserItems_home>() {
                            DateFormat dateFormat = new SimpleDateFormat(date);
                            @Override
                            public int compare(UserItems_home o1, UserItems_home o2) {
                                try {
                                    Date date1 = dateFormat.parse(o1.getIn());
                                    Date date2 = dateFormat.parse(o2.getIn());
                                    return date1.compareTo(date2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });

                        adapter = new historyRecviewAdapter(historyPage.this,dataModelList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        };

         q1.addValueEventListener(listener);
         q2.addValueEventListener(listener);

//        databaseReference.child("in"+"-"+user.getUid()).orderByChild("in").startAt(date).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Process data from path2
//                // ...
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
//                    dataModelList.add(users);
//                }
//
//                adapter = new historyRecviewAdapter(historyPage.this,dataModelList);
//
//                recyclerView.setAdapter(adapter);
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle potential errors here
//            }
//        });
//
//        databaseReference.child("out"+"-"+user.getUid()).orderByChild("in").startAt(date).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Process data from path2
//                // ...
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
//                    dataModelList.add(users);
//                }
//                adapter = new historyRecviewAdapter(historyPage.this,dataModelList);
//
//                recyclerView.setAdapter(adapter);
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle potential errors here
//            }
//        });


       // Collections.reverse(dataModelList);

//        Collections.sort(datalist, new Comparator<UserItems_home>() {
//            DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
//
//            @Override
//            public int compare(UserItems_home data1, UserItems_home data2) {
//                try {
//                    Date date1 = format.parse(data1.getIn());
//                    Date date2 = format.parse(data2.getIn());
//                    return date1.compareTo(date2);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });

    }


    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(historyPage.this, All_item.class);
        startActivity(intent);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
