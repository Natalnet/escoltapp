package br.com.escoltapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.escoltapp.activities.PlaceMapActivity;
import br.com.escoltapp.entities.User;
import br.com.scoltapp.R;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.PlaceHolder>{

    private final Context context;
    private User user;

    public PlacesRecyclerViewAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.place_card_view, viewGroup, false);

        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceHolder placeHolder, int position) {
        placeHolder.label.setText(user.getPlaces().get(position).label);

        placeHolder.itemView.setTag(user.getPlaces().get(position));

        placeHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.Place place = (User.Place)view.getTag();
                Intent intent = new Intent(context,PlaceMapActivity.class);
                intent.putExtra("user",user);
                intent.putExtra("place",place);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return user.getPlaces().size();
    }

    public void addPlace(User.Place place) {
        user.getPlaces().add(place);
        notifyDataSetChanged();
    }

    static class PlaceHolder extends RecyclerView.ViewHolder {
        TextView label;

        PlaceHolder(@NonNull View view) {
            super(view);
            label = view.findViewById(R.id.label);
        }
    }
}
