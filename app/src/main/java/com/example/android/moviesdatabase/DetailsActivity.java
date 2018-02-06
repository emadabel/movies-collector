package com.example.android.moviesdatabase;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.moviesdatabase.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;

import static com.example.android.moviesdatabase.MainActivity.tmdbApi;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.videos;

/**
 * Created by Emad on 13/12/2017.
 */

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<MovieDb> {

    private static final String SEARCH_QUERY_EXTRA = "query";
    private static final int MOVIES_LOADER_ID = 21;

    private ActivityDetailsBinding mDetailBinding;
    //private CastListBinding mCastBinding;

    private CastAdapter mCastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mCastAdapter = new CastAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mDetailBinding.tvCastData.setLayoutManager(layoutManager);
        mDetailBinding.tvCastData.setHasFixedSize(true);
        mDetailBinding.tvCastData.setAdapter(mCastAdapter);

        int extraMovieId = getIntent().getIntExtra("EXTRA_MOVIE_ID", 0);

        LoaderManager.LoaderCallbacks<MovieDb> callback = DetailsActivity.this;

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, callback);

        loadMovieData(extraMovieId);
    }

    private void loadMovieData(int query) {
        if (query == 0) return;

        Bundle queryBundle = new Bundle();
        queryBundle.putInt(SEARCH_QUERY_EXTRA, query);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieDb> moviesSearchLoader = loaderManager.getLoader(MOVIES_LOADER_ID);
        if (moviesSearchLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER_ID, queryBundle, this);
        }
    }

    public int getMetaBgColor(Context context, String metascore) {

        if (metascore.isEmpty() || metascore.equalsIgnoreCase("N/A")) {
            return ContextCompat.getColor(context, R.color.metaNoScore);
        }

        int score = Integer.valueOf(metascore);
        if (score <= 39) {
            return ContextCompat.getColor(context, R.color.meta0to3Score);
        } else if (score <= 60 && score >= 40) {
            return ContextCompat.getColor(context, R.color.meta4to6Score);
        } else if (score >= 61) {
            return ContextCompat.getColor(context, R.color.meta7to10Score);
        } else {
            return ContextCompat.getColor(context, R.color.metaNoScore);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public Loader<MovieDb> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDb>(this) {

            MovieDb mMovie = null;

            @Override
            public void deliverResult(MovieDb movie) {
                mMovie = movie;
                super.deliverResult(movie);
            }

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (mMovie != null) {
                    deliverResult(mMovie);
                } else {
                    mDetailBinding.pbLoadingDetails.setVisibility(View.VISIBLE);
                    mDetailBinding.detailsContainer.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieDb loadInBackground() {
                int query = args.getInt(SEARCH_QUERY_EXTRA);

                if (query == 0) {
                    return null;
                }

                TmdbMovies movies = tmdbApi.getMovies();
                return movies.getMovie(query, "en", credits, videos, images);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDb> loader, MovieDb movieData) {
        if (movieData != null) {
            mDetailBinding.tvMovieTitle.setText(movieData.getTitle());
            //mReleaseYearTextView.setText("(" + movieData.getReleaseDate() + ")");

            StringBuilder stringGeners = new StringBuilder("");
            List<Genre> genres = movieData.getGenres();
            for (Genre gener : genres) {
                stringGeners.append(gener.getName());

                if (genres.indexOf(gener) < genres.size() - 1) {
                    stringGeners.append(", ");
                }
            }

            String movieInfo = movieData.getRuntime() + "   " +
                    stringGeners.toString();
            mDetailBinding.tvMovieInfo.setText(movieInfo);
            mDetailBinding.tvPlot.setText(movieData.getOverview());
            mDetailBinding.tvImdbRating.setText(String.valueOf(movieData.getVoteAverage()));
            mDetailBinding.tvImdbVotes.setText(String.valueOf(movieData.getVoteCount()));

            List<PersonCast> castList = movieData.getCast();
            List<PersonCrew> crewList = movieData.getCrew();

            mCastAdapter.setCastData(castList);

            String directors = "", writers = "";
            for (int i = 0; i < crewList.size(); i++) {
                PersonCrew personCrew = crewList.get(i);

                if (personCrew.getDepartment().equals("Directing")) {
                    if (personCrew.getJob().equals("Director"))
                        directors += personCrew.getName() + ", ";
                } else if (personCrew.getDepartment().equals("Writing")) {
                    if (personCrew.getJob().matches("Author|Writer|Screenplay|Novel"))
                        writers += personCrew.getName() + " (" + personCrew.getJob() + ")" + ", ";
                }
            }

            //mMovieCastTextView.setText("Director: " + directors + "\n" + "Writers: " + writers + "\n" + "Actors: " + " ");

            List<Artwork> images = movieData.getImages();
            List<Video> videos = movieData.getVideos();

            //String metascore = String.valueOf(movieData.getMetascore());
            //int bgColorForMetascore = getMetaBgColor(DetailsActivity.this, metascore);

            //mMetascoreTextView.setText(metascore);
            //mMetascoreTextView.setBackgroundColor(bgColorForMetascore);

            String posterPath = movieData.getPosterPath();
            StringBuilder moviePoster = new StringBuilder(tmdbApi.getConfiguration().getSecureBaseUrl());
            moviePoster.append("w342");
            moviePoster.append(posterPath);

            Picasso.with(DetailsActivity.this).load(moviePoster.toString())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(mDetailBinding.ivMoviePic);
        }
        mDetailBinding.pbLoadingDetails.setVisibility(View.INVISIBLE);
        mDetailBinding.detailsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<MovieDb> loader) {

    }
}
