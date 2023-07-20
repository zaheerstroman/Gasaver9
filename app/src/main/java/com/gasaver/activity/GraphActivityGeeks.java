package com.gasaver.activity;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou.justLoadImage;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.gasaver.R;
import com.gasaver.Response.CatResponse;
import com.gasaver.Response.GraphReportsResponse;
import com.gasaver.adapter.GraphViewPagerAdapter;
import com.gasaver.databinding.ActivityGraphGeeksBinding;
import com.gasaver.model.CategoryModel;
import com.gasaver.model.GraphCitiesModel;
import com.gasaver.model.GraphCitiesResponse;
import com.gasaver.network.APIClient;
import com.gasaver.network.ApiInterface;
import com.gasaver.utils.CommonUtils;
import com.gasaver.utils.Constants;
import com.gasaver.utils.SharedPrefs;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class GraphActivityGeeks extends AppCompatActivity implements View.OnClickListener{
public class GraphActivityGeeks extends AppCompatActivity {

    String cityid = "city";
    LinearLayout loading;

    private ActivityGraphGeeksBinding binding;
    PowerSpinnerView spinner_Graph_City_Related;
    ImageView imageView;

    private ArrayList<CategoryModel> catList = new ArrayList<>();
    private ArrayList<CategoryModel> subcatList = new ArrayList<>();

    private ArrayList<GraphCitiesModel> graphCitiesModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

//        loading = findViewById(R.id.ll_no_data);
//        loading.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("Graph Reports");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });




        spinner_Graph_City_Related = findViewById(R.id.spinner_Graph_City_Related);
        spinner_Graph_City_Related.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                fetchGraphReports();
            }
        });


//        geeksforgeeks:----
        imageView = findViewById(R.id.imageview_Graph);

        //----------------------------------------------------------------------

        getGraphCategories();

    }

    //------------


    private void fetchGraphReports() {

        CommonUtils.showLoading(getApplicationContext(), "Please Wait", false);

        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", SharedPrefs.getInstance(GraphActivityGeeks.this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(GraphActivityGeeks.this).getString(Constants.TOKEN));
        jsonObject.addProperty("city", String.valueOf(spinner_Graph_City_Related.getText()));

        Log.wtf(TAG, jsonObject.toString() );
        Call<GraphReportsResponse> call = apiInterface.fetchGraphReports(jsonObject);

        call.enqueue(new Callback<GraphReportsResponse>() {

            @Override
            public void onResponse(Call<GraphReportsResponse> call, Response<GraphReportsResponse> response) {

                GraphReportsResponse graphReportsResponse = response.body();

                if (graphReportsResponse != null && !graphReportsResponse.getGraphReport().isEmpty()) {

                    if (graphReportsResponse.getMessage() != null && !graphReportsResponse.getMessage().isEmpty()) {

                        // Extract the graph image URL from the response
                        String graphImageUrl = graphReportsResponse.getGraphReport();
                        justLoadImage((Activity) GraphActivityGeeks.this, Uri.parse(graphImageUrl),imageView);

                    }
                }

                else {

//                    Toast.makeText(GraphActivityGeeks.this, "No Graphs", Toast.LENGTH_SHORT).show();
                }
                CommonUtils.hideLoading();
            }

            @Override
            public void onFailure(Call<GraphReportsResponse> call, Throwable t) {

                t.printStackTrace();
                CommonUtils.hideLoading();

                Toast.makeText(GraphActivityGeeks.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(GraphActivityGeeks.this, "No Graphs", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getGraphCategories() {

        CommonUtils.showLoading(GraphActivityGeeks.this, "Please Wait", false);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        com.gasaver.network.ApiInterface apiInterface = APIClient.getClient().create(com.gasaver.network.ApiInterface.class);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("table_name", cityid);
        jsonObject.addProperty("user_id", SharedPrefs.getInstance(GraphActivityGeeks.this).getString(Constants.USER_ID));
        jsonObject.addProperty("token", SharedPrefs.getInstance(GraphActivityGeeks.this).getString(Constants.TOKEN));

        Log.e(TAG,  jsonObject.toString());

        Call<GraphCitiesResponse> call = apiInterface.getGraphData(jsonObject);

        call.enqueue(new Callback<GraphCitiesResponse>() {
            @Override
            public void onResponse(Call<GraphCitiesResponse> call, Response<GraphCitiesResponse> response) {
                try {

                    CommonUtils.hideLoading();


                    graphCitiesModelArrayList = response.body().getData();



                    ArrayList<String> spinnerItems = new ArrayList<>();


                    for (GraphCitiesModel c : response.body().getData()) {
                        spinnerItems.add(c.getName());


                        fetchGraphReports();

                    }



                    //PowerSpinner:-----

                    spinner_Graph_City_Related.setItems(spinnerItems);
                    spinner_Graph_City_Related.selectItemByIndex(0);



                        fetchGraphReports();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GraphCitiesResponse> call, Throwable t) {
                CommonUtils.hideLoading();
            }
        });
    }
}