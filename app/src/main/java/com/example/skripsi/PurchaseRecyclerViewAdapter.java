package com.example.skripsi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Calendar;

public class PurchaseRecyclerViewAdapter extends RecyclerView.Adapter<PurchaseRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<UserItems_home> UserItemsArrayList;
    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseUser user;

    public PurchaseRecyclerViewAdapter(Context context, ArrayList<UserItems_home> userItemsArrayList) {
        this.context = context;
        this.UserItemsArrayList = userItemsArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @Override
    public PurchaseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.purchase_recycler,parent,false);
        return new PurchaseRecyclerViewAdapter.ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PurchaseRecyclerViewAdapter.ViewHolder holder, int position) {

        UserItems_home users = UserItemsArrayList.get(position);

        holder.textName.setText( users.getName());
        holder.Price.setText("Rp."+" "+users.getPrice());
        //holder.textCategory.setText("Item Category: " + users.getCategory());
        //holder.textPrice.setText("Item Price : " + users.getPrice());
        holder.textQuantity.setText("Qty : "+users.getCount());
        // holder.textDateIn.setText("Item Date In : " + users.getIn());
        holder.textDateExpired.setText("Exp : "+users.getExpired());

        holder.Addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PurchaseRecyclerViewAdapter.ViewDIalogUpdate viewDIalogUpdate = new PurchaseRecyclerViewAdapter.ViewDIalogUpdate();
                viewDIalogUpdate.showDialog(context, users.getID(), users.getProductCode(), users.getName(), users.getPrice(), users.getCount(),users.getIn(),users.getExpired());
            }
        });

    }

    @Override
    public int getItemCount() {
        return UserItemsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;

        TextView Price;
        TextView textQuantity;
        TextView textDateExpired;


        ImageButton Addbtn;
        public ViewHolder( View itemView) {
            super(itemView);

            Price = itemView.findViewById(R.id.ProductCode);
            textName = itemView.findViewById(R.id.ItemName);
            textQuantity = itemView.findViewById(R.id.Quantity);
            textDateExpired = itemView.findViewById(R.id.DateExpired);

            Addbtn = itemView.findViewById(R.id.AddButton);
        }
    }

    public class ViewDIalogUpdate{
        int year,month,day;
        int flag=0;
        public void showDialog(Context context, String id,String ProductCode, String Name, String Price, String Quantity, String DateIn, String ExpDate) {
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

                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH)+1;
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    String currentDate =day+"/"+month+"/"+year;

                    String NewQuantity = itemQty.getText().toString();
                    int total;
                    total = (Integer.parseInt(Quantity))-(Integer.parseInt(NewQuantity));

                    if (NewQuantity==null||(Integer.parseInt(NewQuantity))>(Integer.parseInt(Quantity)) ) {
                        Toast.makeText(context, "Maximum qty is "+(Integer.parseInt(Quantity)), Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("stock"+"-"+user.getUid()).child(id).setValue(new UserItems_home(id,ProductCode,Name, Price, (Integer.toString(total)), DateIn, ExpDate));
                        databaseReference.child("out"+"-"+user.getUid()).child(pushId).setValue(new UserItems_home(id,ProductCode,Name, Price, ("-"+NewQuantity), currentDate, ExpDate));
                        Toast.makeText(context, "Item Update Success!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
            flag++;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }

}