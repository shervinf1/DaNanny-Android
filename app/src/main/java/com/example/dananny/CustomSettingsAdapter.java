package com.example.dananny;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomSettingsAdapter extends RecyclerView.Adapter<CustomSettingsAdapter.MyViewHolder> {
    // declaring some fields.
    private ArrayList<SettingsPOJO> settingsList;
    private OnSettingsClickListener listener;


    public CustomSettingsAdapter(ArrayList<SettingsPOJO> settingsList, OnSettingsClickListener listener) {
        this.listener = listener;
        this.settingsList = settingsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsLabel, settingsSubLabel;

        public MyViewHolder(final View itemView) {
            super(itemView);
            settingsLabel = itemView.findViewById(R.id.settingsLabel);
            settingsSubLabel = itemView.findViewById(R.id.settingsSubLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSettingsViewItemClicked(getAdapterPosition(),itemView.getId());
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        SettingsPOJO settings = settingsList.get(position);
        holder.settingsLabel.setText(settings.getSettingsLabel());
        holder.settingsSubLabel.setText(settings.getSettingsSubLabel());
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

}
