package com.example.orientgardenneighbourhoodwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IncidentAdapter extends ArrayAdapter<Incident> {
    public IncidentAdapter(Context context, ArrayList<Incident> incidents) {
        super(context, 0, incidents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Incident currentIncident = getItem(position);

        String stolenItem = currentIncident.getStolenItem();
        TextView stolenItemTextView = listItemView.findViewById(R.id.stolen_item);
        stolenItemTextView.setText(stolenItem);

        String description = currentIncident.getDescription();
        TextView descriptionTextView = listItemView.findViewById(R.id.description);
        descriptionTextView.setText(description);

        String houseNumber = currentIncident.getHouse_number();
        TextView houseNumberTextView = listItemView.findViewById(R.id.complex_number);
        houseNumberTextView.setText(R.string.complex_number + houseNumber);

        return listItemView;
    }
}
