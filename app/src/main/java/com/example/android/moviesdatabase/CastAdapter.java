package com.example.android.moviesdatabase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.people.PersonCast;

import static com.example.android.moviesdatabase.MainActivity.tmdbApi;

/**
 * Created by Emad on 06/02/2018.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context mContext;
    private List<PersonCast> mCastData;

    public CastAdapter(Context context) {
        mContext = context;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutIdForListItem = R.layout.cast_list;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder castViewHolder, int position) {
        String personName = mCastData.get(position).getName();
        castViewHolder.mPersonTextView.setText(personName);

        String character = mCastData.get(position).getCharacter();
        castViewHolder.mCharacterTextView.setText(character);

        String posterPath = mCastData.get(position).getProfilePath();
        StringBuilder personPoster = new StringBuilder(tmdbApi.getConfiguration().getSecureBaseUrl());
        personPoster.append("w185");
        personPoster.append(posterPath);

        Picasso.with(mContext).load(personPoster.toString())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(castViewHolder.mPersonImageView);
    }

    @Override
    public int getItemCount() {
        if (mCastData == null) return 0;
        return mCastData.size();
    }

    public List<PersonCast> getCastData() {
        return mCastData;
    }

    public void setCastData(List<PersonCast> castData) {
        this.mCastData = castData;
        notifyDataSetChanged();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {

        final ImageView mPersonImageView;
        final TextView mPersonTextView;
        final TextView mCharacterTextView;

        public CastViewHolder(View itemView) {
            super(itemView);

            mPersonImageView = (ImageView) itemView.findViewById(R.id.iv_person_poster);
            mPersonTextView = (TextView) itemView.findViewById(R.id.tv_person_name);
            mCharacterTextView = (TextView) itemView.findViewById(R.id.tv_character);
        }
    }
}
