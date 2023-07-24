package com.example.skripsi;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class historyRecviewAdapter  extends RecyclerView.Adapter<historyRecviewAdapter.ViewHolder> {
    Context context;
    ArrayList<UserItems_home> UserItemsArrayList;
    DatabaseReference databaseReference;
    private static final int VIEW_TYPE_TYPE_A = 1;
    private static final int VIEW_TYPE_TYPE_B = 2;



    public historyRecviewAdapter(Context context, ArrayList<UserItems_home> userItemsArrayList) {
        this.context = context;
        this.UserItemsArrayList = userItemsArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }


    @NonNull
    @Override
    public historyRecviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recview_history,parent,false);
        return new historyRecviewAdapter.ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(historyRecviewAdapter.ViewHolder holder, int position) {

        UserItems_home users = UserItemsArrayList.get(position);

        holder.textName.setText(users.getName());
        holder.Price.setText("Rp."+" "+users.getPrice());
        holder.textQuantity.setText("Qty : "+users.getCount());
        holder.textDateExpired.setText("Transaction : "+users.getIn());
    }

    @Override
    public int getItemCount() {
        return UserItemsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        TextView Price;
        TextView textQuantity;
        TextView textDateExpired;

        public ViewHolder( View itemView) {
            super(itemView);

            Price = itemView.findViewById(R.id.ProductCode);
            textName = itemView.findViewById(R.id.ItemName);
            textQuantity = itemView.findViewById(R.id.Quantity);
            textDateExpired = itemView.findViewById(R.id.Transactiondate);

        }
    }

}
