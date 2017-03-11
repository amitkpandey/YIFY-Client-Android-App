package yts.mnf.com.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.robertlevonyan.views.chip.Chip;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yts.mnf.com.Adapter.RecycleAdapter;
import yts.mnf.com.Adapter.SuggestionsAdapter;
import yts.mnf.com.GridSpacingItemDecoration;
import yts.mnf.com.Models.ListModel;
import yts.mnf.com.Models.Movie;
import yts.mnf.com.R;
import yts.mnf.com.Tools.Config;
import yts.mnf.com.Tools.Url;

public class DetailsActivity extends AppCompatActivity {

    Chip chip;
    private SuggestionsAdapter adapter;
    private List<Movie> mModels;
    Context c;
    Gson gson = new Gson();
    ListModel listMode;

    @BindView(R.id.recycler_view_suggestion)
    RecyclerView recyclerViewSuggestion;

    @BindView(R.id.title_movie)
    TextView tvMovie;

    @BindView(R.id.rate_tv)
    TextView tvRate;

    @BindView(R.id.runtime_tv)
    TextView tvTime;

    @BindView(R.id.directed_tv)
    TextView tvDirected;

    @BindView(R.id.desc_tv)
    TextView tvDesc;

    @BindView(R.id.movie_poster_main)
    ImageView posterMain;

    @BindView(R.id.gener_container)
    LinearLayout generContainer;

    @BindView(R.id.sugg_head)
    TextView suggestionTag;

    Movie movieModel;
    static String TAG = "DetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
// set an enter transition
        getWindow().setEnterTransition(new Slide());
// set an exit transition
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        c =this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().hasExtra("movie_json")) {
            Log.e(TAG,"activity has extra ");
            String target = getIntent().getStringExtra("movie_json");
            Log.e(TAG,"activity has extra json =  "+target);
            movieModel = new Gson().fromJson(target, Movie.class);
        }else{
            Log.e(TAG,"activity has no extra ");

        }


        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/FjallaOne-Regular.ttf");
        tvMovie.setTypeface(face);

        Typeface faceRate=Typeface.createFromAsset(getAssets(), "fonts/Righteous-Regular.ttf");
        tvRate.setTypeface(faceRate);

        Typeface faceTime=Typeface.createFromAsset(getAssets(), "fonts/QuattrocentoSans-Regular.ttf");
        tvTime.setTypeface(faceTime);
        tvDirected.setTypeface(faceTime);
        suggestionTag.setTypeface(faceTime);

        Typeface faceDesc=Typeface.createFromAsset(getAssets(), "fonts/Abel-Regular.ttf");
        tvDesc.setTypeface(faceDesc);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        if(movieModel!=null){
            tvMovie.setText(movieModel.getTitle());
            Config.loadImage(posterMain,movieModel.getMediumCoverImage());
            tvDesc.setText(movieModel.getDescriptionFull());
            tvRate.setText(movieModel.getRating().toString());
            tvTime.setText(movieModel.getRuntime()+"ms");
            tvDirected.setText(movieModel.getYear().toString());
            for (int i = 0;i<movieModel.getGenres().size();i++){
                chip = new Chip(getApplication());
                chip.setChipText(movieModel.getGenres().get(i));

                //  chip.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,9));

                generContainer.addView(chip);
            }
            posterMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailAct = new Intent(DetailsActivity.this, ImageViewActivity.class);
                    Pair<View, String> p1 = Pair.create((View)posterMain, "image");
                   // Pair<View, String> p2 = Pair.create((View)holder.movieTitle, "title");

                    detailAct.putExtra("img_url", movieModel.getLargeCoverImage());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(DetailsActivity.this, p1);
                    startActivity(detailAct,options.toBundle());

                }
            });


            mModels = new ArrayList<>();
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recyclerViewSuggestion.setLayoutManager(mLayoutManager);
            recyclerViewSuggestion.addItemDecoration(new GridSpacingItemDecoration(1, Config.dpToPx(1,getApplicationContext()), true));
            //recyclerView.addItemDecoration(new MarginDecoration(this));
            recyclerViewSuggestion.setHasFixedSize(true);



            adapter = new SuggestionsAdapter (c, mModels,getSupportFragmentManager());
            recyclerViewSuggestion.setAdapter(adapter);

            recyclerViewSuggestion.setNestedScrollingEnabled(false);
            startSuggestionRequest(Url.SuggestionUrl+"?movie_id="+movieModel.getId());

        }




    }

    public void startSuggestionRequest(String url){

        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", response.toString());

                        listMode = gson.fromJson(response.toString(),ListModel.class);
                        Log.e("tag","response suggestions "+listMode.getStatus());
                        suggestionTag.setVisibility(View.VISIBLE);
                        mModels = listMode.getData().getMovies();

                      //  Log.e("MainActivity","movieCount = "+movieCount+" movieLimit = "+movieLimit+" totalPage = "+totalPages);
                        adapter.addItems(mModels);
                        recyclerViewSuggestion.setVisibility(View.VISIBLE);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                // hide the progress dialog

            }
        });
        queue.add(jsonObjReq);


    }




    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();

        super.onBackPressed();

    }
}
