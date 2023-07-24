package com.example.skripsi;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Calendar;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<UserItems_home> UserItemsArrayList;
    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseUser user;

    public ItemRecyclerViewAdapter(Context context, ArrayList<UserItems_home> userItemsArrayList) {
        this.context = context;
        this.UserItemsArrayList = userItemsArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_items,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserItems_home users = UserItemsArrayList.get(position);

        holder.textName.setText( users.getName());
        holder.textProductCode.setText("Rp."+" "+users.getPrice());
        holder.textQuantity.setText("Qty : "+users.getCount());
        holder.textDateExpired.setText("Exp : "+users.getExpired());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetail viewItemDetail = new ItemDetail();
                viewItemDetail.showDialog(context, users.getID(), users.getProductCode(), users.getName(),users.getPrice(), users.getCount(),users.getIn(),users.getExpired());
            }
        });

        holder.Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDIalogUpdate viewDIalogUpdate = new ViewDIalogUpdate();
                viewDIalogUpdate.showDialog(context, users.getID(), users.getProductCode(), users.getName(),users.getPrice(), users.getCount(),users.getIn(),users.getExpired());
            }
        });

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDIalogDelete viewDIalogDelete = new ViewDIalogDelete();
                viewDIalogDelete.showDialog(context, users.getID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserItemsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;

        TextView textProductCode;
        TextView textPrice;
        TextView textQuantity;
        //  TextView textDateIn;
        TextView textDateExpired;

        ImageButton Delete;
        ImageButton Update;
        public ViewHolder( View itemView) {
            super(itemView);

            textProductCode = itemView.findViewById(R.id.ProductCode);
            textName = itemView.findViewById(R.id.ItemName);
            textQuantity = itemView.findViewById(R.id.Quantity);
            textDateExpired = itemView.findViewById(R.id.DateExpired);

            Delete = itemView.findViewById(R.id.deleteButton);
            Update = itemView.findViewById(R.id.updateButton);
        }
    }

    public class ItemDetail {
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

    public class ViewDIalogUpdate{

        ImageButton datePick;
        private int year,month,day;

        public void showDialog(Context context, String id,String ProductCode, String Name, String Price, String Quantity, String DateIn, String ExpDate ) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_new_item);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            EditText productCode = dialog.findViewById(R.id.scanText);
            EditText itemName = dialog.findViewById(R.id.AddItemName);
            EditText itemPrice = dialog.findViewById(R.id.AddPrice);
            EditText itemQty = dialog.findViewById(R.id.AddQuantity);
            TextView itemDateIn = dialog.findViewById(R.id.AddDateIn);
            TextView itemExpDate = dialog.findViewById(R.id.AddDateExpired);
            datePick = dialog.findViewById(R.id.date_picker_actions);
            String pushId = databaseReference.push().getKey();



            productCode.setText(ProductCode);
            itemName.setText(Name);
            itemPrice.setText(Price);
            itemQty.setText(Quantity);
            itemDateIn.setText(DateIn);
            itemExpDate.setText(ExpDate);

            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);

            itemExpDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH)+1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog;
                    dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth) {
                            year = year1;
                            month = month1;
                            day = dayOfMonth;

                            itemExpDate.setText(day+"/"+month+"/"+year);
                        }
                    },year,month,day);
                    dialog.show();
                }
            });

            datePick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH)+1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog;
                    dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth) {
                            year = year1;
                            month = month1;
                            day = dayOfMonth;

                            itemExpDate.setText(day+"/"+month+"/"+year);
                        }
                    },year,month,day);
                    dialog.show();
                }
            });

            buttonUpdate.setText("Update");
            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String ProductCode = productCode.getText().toString();
                    String NewName = itemName.getText().toString();
                    String NewPrice = itemPrice.getText().toString();
                    String NewQuantity = itemQty.getText().toString();
                    String NewDateIn = itemDateIn.getText().toString();
                    String NewExpDate = itemExpDate.getText().toString();

                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH)+1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    String currentDate =day+"/"+month+"/"+year;

                    String finalcount;
                    if(Integer.parseInt(Quantity)<Integer.parseInt(NewQuantity)){
                        finalcount = "+"+(Integer.parseInt(NewQuantity)-Integer.parseInt(Quantity));
                    }else{
                        finalcount = "-"+(Integer.parseInt(Quantity)-Integer.parseInt(NewQuantity));
                    }


                    if (NewName.isEmpty() || NewPrice.isEmpty() || Integer.parseInt(NewQuantity)==0 || NewDateIn.isEmpty()) {
                        Toast.makeText(context, "Please insert all data...", Toast.LENGTH_SHORT).show();
                    } else {

                        if(Integer.parseInt(Quantity)<Integer.parseInt(NewQuantity)){
                            databaseReference.child("in"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,NewName, NewPrice, finalcount, currentDate, NewExpDate));
                        }else{
                            databaseReference.child("out"+"-"+user.getUid()).child(pushId).setValue(new UserItems_home(id,ProductCode,NewName, NewPrice, finalcount, currentDate, NewExpDate));
                        }
                        databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,NewName, NewPrice, NewQuantity, NewDateIn, NewExpDate));
                        Toast.makeText(context, "Item Update Success!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }

    }

    public class ViewDIalogDelete{
        public void showDialog(Context context, String id) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.delete_confim_dialog);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button CancelButton = dialog.findViewById(R.id.buttonCancel);

            buttonDelete.setText("DELETE");
            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    databaseReference.child("in"+"-"+user.getUid()).child(id).removeValue();
                    databaseReference.child("stock"+"-"+user.getUid()).child(id).removeValue();
                    Toast.makeText(context, "Item Delete Success!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}

