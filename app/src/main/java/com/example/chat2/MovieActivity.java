package com.example.chat2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MovieActivity extends AppCompatActivity {

    public class GetMovieDetails extends AsyncTask<String, Void, Object>{

        private String colour;
        @Override
        protected Object doInBackground(String... strings) {
            String link = strings[0];
            DetailedMovie detailedMovie = null;

            URL url = null;
            try {
                url = new URL(link);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }

                String text = builder.toString();
                JSONObject jsonObject = new JSONObject(text);
                String title = jsonObject.getString("Title");
                int year = jsonObject.getInt("Year");
                String released = jsonObject.getString("Released");
                String runtime = jsonObject.getString("Runtime");
                String genre = jsonObject.getString("Genre");
                String country = jsonObject.getString("Country");
                String awards = jsonObject.getString("Awards");
                String language = jsonObject.getString("Language");
                String response = jsonObject.getString("Response");

                if(response.equals("True")){
                    detailedMovie = new DetailedMovie(title, year, released, runtime, genre, country, awards, language, response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return detailedMovie;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        String link = "http://www.omdbapi.com/?t=%22\"" + movie.getTitle() + "\"&apikey=62cbdd62";

        SharedPreferences sp = getSharedPreferences("text", MODE_PRIVATE);
        String col = sp.getString("Colour", "white");
        setColour(col);

        final TextView year = findViewById(R.id.tvMovieYear_movieAct);
        final TextView released = findViewById(R.id.tvMovieReleased_movieAct);
        final TextView runtime = findViewById(R.id.tvMovieRuntime_movieAct);
        final TextView genre = findViewById(R.id.tvMovieGenre_movieAct);
        final TextView country = findViewById(R.id.tvMovieCountry_movieAct);
        final TextView awards = findViewById(R.id.tvMovieAwards_movieAct);
        final TextView language = findViewById(R.id.tvMovieLanguage_movieAct);

        GetMovieDetails getMovieDetails = new GetMovieDetails(){
            @Override
            protected void onPostExecute(Object o) {
                TextView title = findViewById(R.id.tvTitle_movieAct);
                if(o != null) {
                    DetailedMovie detailedMovie = (DetailedMovie) o;

                    title.setText(detailedMovie.getTitle());
                    year.setText("" + detailedMovie.getYear());
                    released.setText(detailedMovie.getReleased());
                    runtime.setText(detailedMovie.getRuntime());
                    genre.setText(detailedMovie.getGenre());
                    country.setText(detailedMovie.getCountry());
                    awards.setText(detailedMovie.getAwards());
                    language.setText(detailedMovie.getLanguage());

                }else{
                    title.setText(R.string.wrongMovieTitle);
                }
            }
        };

        getMovieDetails.execute(link);
    }

    public void savePreferenceces(String colour){
        SharedPreferences sp = getSharedPreferences("text", MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Colour", colour);
        editor.commit();
    }

    public void setColour(String colour){

         TextView year = findViewById(R.id.tvMovieYear_movieAct);
         TextView released = findViewById(R.id.tvMovieReleased_movieAct);
         TextView runtime = findViewById(R.id.tvMovieRuntime_movieAct);
         TextView genre = findViewById(R.id.tvMovieGenre_movieAct);
         TextView country = findViewById(R.id.tvMovieCountry_movieAct);
         TextView awards = findViewById(R.id.tvMovieAwards_movieAct);
         TextView language = findViewById(R.id.tvMovieLanguage_movieAct);

         int c ;
          if(colour.equals("white")){
              c =getResources().getColor(R.color.colorWhitePure);
          }else{
              c =(getResources().getColor(R.color.colorViolet1));
          }

        year.setTextColor(c);
        released.setTextColor(c);
        runtime.setTextColor(c);
        genre.setTextColor(c);
        country.setTextColor(c);
        awards.setTextColor(c);
        language.setTextColor(c);
    }
    public void setTextColourWhite(View view) {
        savePreferenceces("white");
        setColour("white");
    }

    public void setTextColourPurple(View view) {
        savePreferenceces("purple");
        setColour("purple");
    }
}
