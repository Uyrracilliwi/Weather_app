package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseQuery;

import java.util.List;

/**
* This class is the adapter for an individual location.  
**/ 
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    Context context;
    List<Location> locations;

    public LocationsAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    /**
    * This method creates a new ViewHolder
    **/ 
    @NonNull
    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_location xml layout for this fragment
        View view = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }
    
    /**
    * This method updates the contents of the ItemView to reflect the location and its respective position.
    **/ 
    @Override
    public void onBindViewHolder(@NonNull LocationsAdapter.ViewHolder holder, int position) {
        Location loc = locations.get(position);
        holder.bind(loc);
    }

    /**
    * This method retrieves the size of the location list.
    **/ 
    @Override
    public int getItemCount() {
        return locations.size();
    }

    /**
    * This class represents the ViewHolder.
    **/ 
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLocationName;
        private Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Search for tvLocationName within item_location
            tvLocationName = itemView.findViewById(R.id.tvLocationName);

            // Search for btnDetail within item_location
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        /**
        * This method binds the location data to the Viewholder elements.
        **/ 
        public void bind(Location loc) {

            // Set text to name of location.
            tvLocationName.setText(loc.getName());

            // Protocol for when user clicks on btnDetail
            btnDetail.setOnClickListener(new View.OnClickListener() {
                
                // This method binds the location with a specified id.
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, LocationDetailsActivity.class);
                    i.putExtra("LOCATION_ID", loc.getObjectId());
                    // Start LocationDetailsActivity
                    context.startActivity(i);
                }
            });
        }
    }
}
