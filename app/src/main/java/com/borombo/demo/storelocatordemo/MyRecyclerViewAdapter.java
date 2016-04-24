package com.borombo.demo.storelocatordemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Erwan on 19/04/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder>{

    private ArrayList<Restaurant> cardData;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView name;
        protected TextView adresse;
        protected TextView distance;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            adresse = (TextView) itemView.findViewById(R.id.adresse);
            distance = (TextView) itemView.findViewById(R.id.distance);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<Restaurant> cardData) {
        this.cardData = cardData;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Restaurant data = cardData.get(position);
        holder.name.setText(data.getNom());
        holder.adresse.setText(data.getAdresse());
        holder.distance.setText(formatDistance(data));
    }

    public String formatDistance(Restaurant data){
        float distanceValue = data.getDistanceToUser();
        String distanceUnit = data.getDistanceUnit();
        if (distanceUnit.equals("km")){
            distanceValue /= 1000;
        }
        return String.format("%.2f %s", distanceValue, distanceUnit);
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    interface MyClickListener {
        void onItemClick(int position, View v);
    }

}
