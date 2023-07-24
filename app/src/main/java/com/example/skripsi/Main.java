package com.example.skripsi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class Main extends AppCompatActivity {
    FirebaseAuth auth;
    ImageButton Logoutbtn;
    TextView textView,viewAll;
    FirebaseUser user;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ArrayList<UserItems_home> userItemsArrayList;
    ItemRecyclerViewAdapter Adaptor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ImageView AddButton,LowStockBtn, Purchase, Scan_QR,notifBadge;
    String ProductCode;
    private UserSessionManager preferenceManager;

    //EditText searchText;

    int number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        preferenceManager = new UserSessionManager(this);
        auth = FirebaseAuth.getInstance();
        Logoutbtn = findViewById(R.id.LogoutButton);
        textView = findViewById(R.id.UserAccount);
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
            firebaseDatabase.setPersistenceEnabled(true);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }


        Logoutbtn.setOnClickListener(v -> {
                ViewDIalogLogout viewDIalogDelete = new ViewDIalogLogout();
                viewDIalogDelete.showDialog(Main.this);

        });
//===============================login page ^============================================================================

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userItemsArrayList = new ArrayList<>();

        //searchText = findViewById(R.id.searchfield);
        AddButton = findViewById(R.id.imageButton1);
        LowStockBtn = findViewById(R.id.imageButton3);
        Purchase = findViewById(R.id.PurchaseBtn);
        Scan_QR = findViewById(R.id.scanQRbtn);
        notifBadge = findViewById(R.id.notifBadge);
        viewAll = findViewById(R.id.viewAll);


        AddButton.setOnClickListener(view -> {
            ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
            viewDialogAdd.showDialog(Main.this);
        });

        LowStockBtn.setOnClickListener(view->{
            Intent intent = new Intent(Main.this, LowStockAct.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            notifBadge.setVisibility(View.INVISIBLE);
        });

        viewAll.setOnClickListener(view->{
            Intent intent = new Intent(Main.this, All_item.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Purchase.setOnClickListener(view->{
            Intent intent = new Intent(Main.this, TransactionPage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Scan_QR.setOnClickListener(v -> Scanner());

        readData();


//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                View childView = recyclerView.findChildViewUnder(e.getX(0), e.getY(0));
//
//                if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
//                    int position = recyclerView.getChildAdapterPosition(childView);
//                    UserItems_home clickedItem = userItemsArrayList.get(position);
//                    // Perform your action with the clicked item
//                    ViewItemDetail viewItemDetail = new ViewItemDetail();
//                    viewItemDetail.showDialog(Main.this,clickedItem.getID(),clickedItem.getProductCode(),clickedItem.getName(),clickedItem.getPrice(),clickedItem.getCount(),clickedItem.getIn(),clickedItem.getExpired());
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });

//        searchText.addTextChangedListener(new TextWatcher() {
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
//                    readData(s.toString());
//
//                }else{
//                    readData("");
//
//
//                }
//            }
//        });
    }

    private void Scanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR/Barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.getCaptureActivity();
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {

        if (result.getContents()!=null)
        {
            ProductCode = result.getContents();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stock"+"-"+user.getUid());
            Query query = databaseRef.orderByChild("productCode").equalTo(ProductCode);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            // Data with the specified ID exist
                        UserItems_home users = snapshot.getValue(UserItems_home.class);

                        ViewDialogScanUpdt viewDialogScanUpdt = new ViewDialogScanUpdt();
                        viewDialogScanUpdt.showDialog(Main.this, users.getID(),users.getProductCode(),users.getName(),users.getPrice(),users.getCount(),users.getIn(),users.getExpired());
                        }
                       } else {
                        // Data with the specified ID doesn't exist
                        ViewDialogScanAdd viewDialogAdd = new ViewDialogScanAdd();
                        viewDialogAdd.showDialog(Main.this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
              });
        }
    });


    private void readData() {
        int year,month,day;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day+"/"+month+"/"+year;

        databaseReference.child("stock"+"-"+user.getUid()).orderByChild("in").endAt(currentDate).limitToLast(4).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot) {
                userItemsArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserItems_home users = dataSnapshot.getValue(UserItems_home.class);
                    assert users != null;
                    number = Integer.parseInt(users.getCount());
                    if(number >0){
                        userItemsArrayList.add(users);
                        if(number<5){
                            if(dataSnapshot.exists()){
                                notifBadge.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else{
                        userItemsArrayList.remove(users);
                    }
                }

                Adaptor = new ItemRecyclerViewAdapter(Main.this,userItemsArrayList);
                Collections.reverse(userItemsArrayList);
                recyclerView.setAdapter(Adaptor);

                Adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error) {

            }
        });
    }

//    public class ViewItemDetail{
//        public void showDialog(Context context, String id,String ProductCode, String Name,String Price, String Quantity, String DateIn, String ExpDate ) {
//            final Dialog dialog = new Dialog(context);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setCancelable(false);
//            dialog.setContentView(R.layout.user_items_detail);
//
//            auth = FirebaseAuth.getInstance();
//            user = auth.getCurrentUser();
//
//            TextView productCode = dialog.findViewById(R.id.detProductCode);
//            TextView itemName = dialog.findViewById(R.id.detItemName);
//            //TextView itemCategory = dialog.findViewById(R.id.detCategory);
//            TextView itemPrice = dialog.findViewById(R.id.detPrice);
//            TextView itemQty = dialog.findViewById(R.id.detQuantity);
//            TextView itemDateIn = dialog.findViewById(R.id.detDateIn);
//            TextView itemExpDate = dialog.findViewById(R.id.detDateExpired);
//
//            productCode.setText(ProductCode);
//            itemName.setText(Name);
//            //itemCategory.setText(Category);
//            itemPrice.setText("Rp."+" "+Price);
//            itemQty.setText(Quantity);
//            itemDateIn.setText(DateIn);
//            itemExpDate.setText(ExpDate);
//
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//            dialog.setCanceledOnTouchOutside(true);
//            dialog.onBackPressed();
//            dialog.show();
//
//        }
//
//    }

    public class ViewDIalogLogout{
        public void showDialog(Context context) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.delete_confim_dialog);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);
            TextView text = dialog.findViewById(R.id.textTitle);
            CancelButton.setText("NO");
            text.setText("Are you sure to Log Out ?");
            buttonDelete.setText("Yes");

            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserSessionManager sessionManager = new UserSessionManager(getApplicationContext());
                    sessionManager.setLoggedIn(false);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class ViewDialogAdd{
        private int year,month,day;
        @SuppressLint("SetTextI18n")
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_new_item);

            EditText productCode = dialog.findViewById(R.id.scanText);
            EditText itemName = dialog.findViewById(R.id.AddItemName);
           //EditText itemCategory = dialog.findViewById(R.id.AddCategory);
            EditText itemPrice = dialog.findViewById(R.id.AddPrice);
            EditText itemQty = dialog.findViewById(R.id.AddQuantity);
            TextView itemDateIn = dialog.findViewById(R.id.AddDateIn);
            TextView itemExpDate = dialog.findViewById(R.id.AddDateExpired);
            ImageButton datePick = dialog.findViewById(R.id.date_picker_actions);

            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            day = calendar.get(Calendar.DAY_OF_MONTH);

            itemDateIn.setText(day+"/"+month+"/"+year);

            Button AddButton = dialog.findViewById(R.id.buttonAdd);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);

            AddButton.setText("ADD");

            itemExpDate.setOnClickListener(v -> {
                Calendar calendar12 = Calendar.getInstance();
                year = calendar12.get(Calendar.YEAR);
                month = calendar12.get(Calendar.MONTH)+1;
                day = calendar12.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog12;
                dialog12 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog12.show();
            });

            datePick.setOnClickListener(v -> {
                Calendar calendar1 = Calendar.getInstance();
                year = calendar1.get(Calendar.YEAR);
                month = calendar1.get(Calendar.MONTH)+1;
                day = calendar1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog1;
                dialog1 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog1.show();
            });

            CancelButton.setOnClickListener(view -> dialog.dismiss());

            AddButton.setOnClickListener(view -> {
                String id = "item" + new Date().getTime();
                String ProductCode = productCode.getText().toString();
                String Name = itemName.getText().toString().toLowerCase();
               // String Category = itemCategory.getText().toString().toLowerCase();
                String Price = itemPrice.getText().toString();
                String Quantity = itemQty.getText().toString();
                String DateIn = itemDateIn.getText().toString();
                String ExpDate = itemExpDate.getText().toString();

               SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("stock"+"-"+user.getUid());
                Query query = databaseRef.orderByChild("productCode").equalTo(ProductCode);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                // Data with the specified ID exist
                                Toast.makeText(context,"Failed to add Data! Data already exist",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Data with the specified ID doesn't exist
                            if(Name.isEmpty()||Price.isEmpty()||Quantity.isEmpty()||DateIn.isEmpty()){
                                Toast.makeText(context,"Please insert all valid data...",Toast.LENGTH_SHORT).show();
                            }else {
                                databaseReference.child("in"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,Name,Price,("+"+Quantity),DateIn,ExpDate));
                                databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,Name,Price,Quantity,DateIn,ExpDate));
                                Toast.makeText(context,"DONE!",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });


            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
//====================================================================================================================================================================
    public class ViewDialogScanAdd{
        private int year,month,day;
        String code = ProductCode;

        @SuppressLint("SetTextI18n")
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_new_item);

            EditText productCode = dialog.findViewById(R.id.scanText);
            EditText itemName = dialog.findViewById(R.id.AddItemName);
            //EditText itemCategory = dialog.findViewById(R.id.AddCategory);
            EditText itemPrice = dialog.findViewById(R.id.AddPrice);
            EditText itemQty = dialog.findViewById(R.id.AddQuantity);
            TextView itemDateIn = dialog.findViewById(R.id.AddDateIn);
            TextView itemExpDate = dialog.findViewById(R.id.AddDateExpired);
            ImageButton datePick = dialog.findViewById(R.id.date_picker_actions);

            productCode.setText(code);

            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            day = calendar.get(Calendar.DAY_OF_MONTH);

            itemDateIn.setText(day+"/"+month+"/"+year);

            Button AddButton = dialog.findViewById(R.id.buttonAdd);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);

            AddButton.setText("ADD");

            itemExpDate.setOnClickListener(v -> {
                Calendar calendar1 = Calendar.getInstance();
                year = calendar1.get(Calendar.YEAR);
                month = calendar1.get(Calendar.MONTH)+1;
                day = calendar1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog1;
                dialog1 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog1.show();
            });

            datePick.setOnClickListener(v -> {
                Calendar calendar12 = Calendar.getInstance();
                year = calendar12.get(Calendar.YEAR);
                month = calendar12.get(Calendar.MONTH)+1;
                day = calendar12.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog12;
                dialog12 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog12.show();
            });


            CancelButton.setOnClickListener(view -> dialog.dismiss());

            AddButton.setOnClickListener(view -> {
                String id = "item" + new Date().getTime();
                String ProductCode = productCode.getText().toString();
                String Name = itemName.getText().toString().toLowerCase();
               // String Category = itemCategory.getText().toString().toLowerCase();
                String Price = itemPrice.getText().toString();
                String Quantity = itemQty.getText().toString();
                String DateIn = itemDateIn.getText().toString();
                String ExpDate = itemExpDate.getText().toString();

                if(Name.isEmpty()||Price.isEmpty()||Quantity.isEmpty()||DateIn.isEmpty()){
                    Toast.makeText(context,"Please insert all valid data...",Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("in"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,Name,Price,("+"+Quantity),DateIn,ExpDate));
                    databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,Name,Price,Quantity,DateIn,ExpDate));
                    Toast.makeText(context,"DONE!",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
//===========================================================================================================================================================

    public class ViewDialogScanUpdt {
        private int year,month,day;


        @SuppressLint("SetTextI18n")
        public void showDialog(Context context, String id,String ProductCode, String Name,String Price, String Quantity, String DateIn, String ExpDate){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_new_item);

            EditText productCode = dialog.findViewById(R.id.scanText);
            EditText itemName = dialog.findViewById(R.id.AddItemName);
           // EditText itemCategory = dialog.findViewById(R.id.AddCategory);
            EditText itemPrice = dialog.findViewById(R.id.AddPrice);
            EditText itemQty = dialog.findViewById(R.id.AddQuantity);
            TextView itemDateIn = dialog.findViewById(R.id.AddDateIn);
            TextView itemExpDate = dialog.findViewById(R.id.AddDateExpired);
            ImageButton datePick = dialog.findViewById(R.id.date_picker_actions);
            String pushId = databaseReference.push().getKey();

            itemName.setText(Name);
            //itemCategory.setText(Category);
            itemPrice.setText(Price);
            itemQty.setText("1");
            itemDateIn.setText(DateIn);
            itemExpDate.setText(ExpDate);
            productCode.setText(ProductCode);

            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            day = calendar.get(Calendar.DAY_OF_MONTH);

            itemDateIn.setText(day+"/"+month+"/"+year);

            Button AddButton = dialog.findViewById(R.id.buttonAdd);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);

            AddButton.setText("ADD");

            itemExpDate.setOnClickListener(v -> {
                Calendar calendar1 = Calendar.getInstance();
                year = calendar1.get(Calendar.YEAR);
                month = calendar1.get(Calendar.MONTH)+1;
                day = calendar1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog1;
                dialog1 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog1.show();
            });

            datePick.setOnClickListener(v -> {
                Calendar calendar12 = Calendar.getInstance();
                year = calendar12.get(Calendar.YEAR);
                month = calendar12.get(Calendar.MONTH)+1;
                day = calendar12.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog12;
                dialog12 = new DatePickerDialog(Main.this, (view, year1, month1, dayOfMonth) -> {
                    year = year1;
                    month = month1;
                    day = dayOfMonth;

                    itemExpDate.setText(day+"/"+month+"/"+year);
                },year,month,day);
                dialog12.show();
            });


            CancelButton.setOnClickListener(view -> dialog.dismiss());

            AddButton.setOnClickListener(view -> {
                String newProductCode = productCode.getText().toString();
                String newName = itemName.getText().toString().toLowerCase();
                //String newCategory = itemCategory.getText().toString().toLowerCase();
                String newPrice = itemPrice.getText().toString();
                String newQuantity = itemQty.getText().toString();
                String newDateIn = itemDateIn.getText().toString();
                String newExpDate = itemExpDate.getText().toString();

                int x = Integer.parseInt(Quantity);
                int z = Integer.parseInt(newQuantity);
                int total = (x+z);
                String sum = Integer.toString(total);

                if(Name.isEmpty()||Price.isEmpty()||Quantity.isEmpty()||Integer.parseInt(Quantity)<=0||DateIn.isEmpty()){
                    Toast.makeText(context,"Please insert all valid data...",Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("in"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,newProductCode,newName,newPrice,("+"+newQuantity),newDateIn,newExpDate));
                    databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,newProductCode,newName,newPrice,sum,newDateIn,newExpDate));
                    Toast.makeText(context,"DONE!",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}