package com.example.skripsi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TransactionPage extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private PurchaseRecyclerViewAdapter adapter;
    private ArrayList<UserItems_home> dataModelList;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    int number;
    EditText searchBar;
    ImageButton Scan_QR;
    String ProductCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_activity);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataModelList = new ArrayList<>();
        adapter = new PurchaseRecyclerViewAdapter(TransactionPage.this,dataModelList);
        recyclerView.setAdapter(adapter);
        searchBar = findViewById(R.id.searchfield);
        Scan_QR = findViewById(R.id.scanQRbtn);

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

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()!=null){
                    readData(s.toString().toLowerCase());
                }else{
                    readData("");
                }
            }
        });

        Scan_QR.setOnClickListener(v -> Scanner());
    }

    private void Scanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR/Barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.getCaptureActivity();
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents()!=null)
        {
            ProductCode = result.getContents();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stock"+"-"+user.getUid());
            Query query = databaseRef.orderByChild("productCode").equalTo(ProductCode);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            // Data with the specified ID exist
                            UserItems_home users = snapshot.getValue(UserItems_home.class);

                            ViewDialogScanUpdt viewDialogScanUpdt = new ViewDialogScanUpdt();
                            viewDialogScanUpdt.showDialog(TransactionPage.this, users.getID(),users.getProductCode(),users.getName(),users.getPrice(),users.getCount(),users.getIn(),users.getExpired());
                        }
                    }else{
                            Toast.makeText(TransactionPage.this, "Cannot find data !", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });

        }
    });


    private void readData(String data) {

        databaseReference.child("stock"+"-"+user.getUid()).orderByChild("name").startAt(data).endAt(data+"\uf8ff").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot) {
                dataModelList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
                    number = Integer.parseInt(users.getCount());
                    if(number>0){
                        dataModelList.add(users);
                    }
                }
                adapter = new PurchaseRecyclerViewAdapter(TransactionPage.this,dataModelList);

                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public class ViewDialogScanUpdt {
        public void showDialog(Context context, String id, String ProductCode, String Name, String Price, String Quantity, String DateIn, String ExpDate) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.sold_item_dialog);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();



            EditText itemQty = dialog.findViewById(R.id.soldQty);

            itemQty.setText(Quantity);

            Button buttonUpdate = dialog.findViewById(R.id.stockUpdt);
            Button CancelButton = dialog.findViewById(R.id.CancelStockUpdt);

            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pushId = databaseReference.push().getKey();

                    String NewQuantity = itemQty.getText().toString();
                    int total;
                    total = (Integer.parseInt(Quantity)) - (Integer.parseInt(NewQuantity));


                    if (NewQuantity == null||(Integer.parseInt(NewQuantity))>(Integer.parseInt(Quantity))) {
                        Toast.makeText(context, "Maximum qty is "+(Integer.parseInt(Quantity)), Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("out"+"-"+user.getUid()).child(pushId).child(id).setValue(new UserItems_home(id, ProductCode, Name, Price, (Integer.toString(total)), DateIn, ExpDate));
                        databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id, ProductCode, Name, Price, (Integer.toString(total)), DateIn, ExpDate));
                        Toast.makeText(context, "Item Sold!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}
