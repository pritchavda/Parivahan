package com.example.parivahan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parivahan.HistoryRecyclerView.HistoryObject;
import com.example.parivahan.HistoryRecyclerView.HistoryViewHolders;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<HistoryViewHolders> {

    private List<HistoryObject> userList;

    public Adapter(List<HistoryObject>userList) {
        this.userList=userList;
    }


    @NonNull
    @Override
    public HistoryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_histomer,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryViewHolders rcv = new HistoryViewHolders(layoutView);
        return rcv;


    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolders holder, int position) {

//        int resource = userList.get(position).getImageview();
//        String name=userList.get(position).getTextview1();
//        String msg=userList.get(position).getTextview2();
//        String time=userList.get(position).getTextview3();
//        String line=userList.get(position).getDivider();
//        String rr = userList.get(position).getRideId();
        holder.rideId.setText(userList.get(position).getRideId());
        holder.time.setText(userList.get(position).getTime());
//        holder.setData(rr);



    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class



    public class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView;
//        private TextView textView;
//        private TextView textView2;
//        private TextView textview3;
//        private TextView divider;
    public TextView rideId;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //here use xml ids
            //give different name not like constructor
//            imageView=itemView.findViewById(R.id.imageview);
//            textView=itemView.findViewById(R.id.textview);
//            textView2=itemView.findViewById(R.id.textview2);
//            textview3=itemView.findViewById(R.id.textview3);
//            divider=itemView.findViewById(R.id.Divider);
            rideId = (TextView) itemView.findViewById(R.id.rideId);
        }

//        public void setData(int resource, String name, String msg, String time,String line) {

//            imageView.setImageResource(resource);
//            textView.setText(name);
//            textView2.setText(msg);
//            textview3.setText(time);
//            divider.setText(line);

//        }
        public void setData(String rr) {

//            imageView.setImageResource(resource);
//            textView.setText(name);
//            textView2.setText(msg);
//            textview3.setText(time);
//            divider.setText(line);
            rideId.setText(rr);

        }
    }
}
