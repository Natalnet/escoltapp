package br.com.escoltapp.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.util.BitmapUtil;
import br.com.scoltapp.R;

public class FollowRecyclerViewAdapter extends RecyclerView.Adapter<FollowRecyclerViewAdapter.FollowHolder>{

    private List<User> follow;

    public FollowRecyclerViewAdapter(List<User> follow) {
        this.follow = follow;
    }

    @NonNull
    @Override
    public FollowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView view = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_card_view, viewGroup, false);
        return new FollowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowHolder followHolder, int i) {

        if (follow.get(i).getPhotoBitmap() == null) {
            BitmapUtil.downloadImage(follow.get(i).getPhotoUrl(), new BitmapUtil.OnFinishDownloadListener() {
                @Override
                public void onFinishDownload(Bitmap bitmap) {
                    follow.get(followHolder.getAdapterPosition()).setPhotoBitmap(bitmap);
                    followHolder.progress.setVisibility(View.GONE);
                    followHolder.photo.setVisibility(View.VISIBLE);
                    followHolder.photo.setImageBitmap(follow.get(followHolder.getAdapterPosition()).getPhotoBitmap());
                }
            });
        } else {
            followHolder.photo.setImageBitmap(follow.get(i).getPhotoBitmap());
        }
        followHolder.name.setText(String.format("%s %s", follow.get(i).getFirstName(), follow.get(i).getLastName()));
        followHolder.phone.setText(follow.get(i).getPhone());
    }

    @Override
    public int getItemCount() {
        return follow.size();
    }

    public void addUser(User user) {
        follow.add(user);
        notifyDataSetChanged();
    }

    static class FollowHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        TextView phone;
        ProgressBar progress;

        FollowHolder(@NonNull View view) {
            super(view);

            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            progress = view.findViewById(R.id.progress);
        }
    }
}
