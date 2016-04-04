package com.YC2010.Fotag;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.util.ArrayList;

/**
 * Created by jason on 3/26/2016.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private ArrayList<ImageItem> mImages;

    /***** Creating OnItemClickListener *****/

    // Define listener member variable
    private static OnItemClickListener listener;
    private static OnRatingBarChangeListener ratingBarListener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    public interface OnRatingBarChangeListener {
        void onRatingChanged(int rating, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.ratingBarListener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public RatingBar mRatingBar;
        public ViewHolder(View v){
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.cardview_image);
            mRatingBar = (RatingBar) v.findViewById(R.id.ratingBar);

            // Setup the click listener
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(v, getLayoutPosition());
                }
            });

            mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (ratingBarListener != null)
                        ratingBarListener.onRatingChanged((int) rating, getLayoutPosition());
                }
            });
        }
    }

    public CourseAdapter(ArrayList<ImageItem> images) {
        mImages = images;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImageView.setImageDrawable(mImages.get(position).getmDrawable());
        holder.mImageView.setTransitionName("transition " + position);
        holder.mRatingBar.setRating(mImages.get(position).getRating());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void updateList() {
        mImages = Model.getInstance().getImages();
    }

    public void changeRating(int rating, int position){
        Log.d("CourseAdapter", "rating change to " + rating);
        Model.getInstance().updateRatingByUUID(mImages.get(position).getmUUID(), rating);
        if (rating < Model.getInstance().getRating()){
            mImages.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ArrayList<ImageItem> getmImages() {
        return mImages;
    }

    public void updateListWithNotify() {
        mImages = Model.getInstance().getImages();
        notifyDataSetChanged();
    }
}
