package com.bw.quoteoftheday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView quoteTextView;
    private Button shareButton;
    private Button favoriteButton;
    private Button viewFavoritesButton;
    private ArrayList<String> quotes;
    private String currentQuote;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "QuotePrefs";
    private static final String FAVORITE_QUOTES_KEY = "FavoriteQuotes";
    private static final String CURRENT_QUOTE_KEY = "CurrentQuote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTextView = findViewById(R.id.titleTextView);
        quoteTextView = findViewById(R.id.quoteTextView);
        shareButton = findViewById(R.id.shareButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        viewFavoritesButton = findViewById(R.id.viewFavoritesButton);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        titleTextView.setText("Quote Of The Day");

        quotes = new ArrayList<>();
        quotes.add("If people knew how hard I worked to get my mastery, it wouldn't seem so wonderful after all. — Michelangelo");
        quotes.add("Before you start some work, always ask yourself three questions - Why am I doing it, What the results might be and Will I be successful. Only when you think deeply and find satisfactory answers to these questions, go ahead. — Chanakya");
        quotes.add("Do the best you can until you know better. Then when you know better, do better. — Maya Angelou");
        quotes.add("Success is no accident. It is hard work, perseverance, learning, studying, sacrifice and most of all, love of what you are doing or learning to do. — Pele");
        quotes.add("I do not know anyone who has got to the top without hard work. That is the recipe. It will not always get you to the top, but should get you pretty near. — Margaret Thatcher");
        quotes.add("Hard work spotlights the character of people: some turn up their sleeves, some turn up their noses, and some don’t turn up at all. — Sam Ewing");
        quotes.add("The only thing standing between you and outrageous success is continuous progress you need discipline. — Dan Waldschmidt");
        quotes.add("Talent is cheaper than table salt. What separates the talented individual from the successful one is a lot of hard work. — Stephen King");
        quotes.add("Hard work is a prison sentence only if it does not have meaning. Once it does, it becomes the kind of thing that makes you grab your wife around the waist and dance a jig. — Malcolm Gladwell");
        quotes.add("The fight is won or lost far away from witnesses — behind the lines, in the gym, and out there on the road, long before I dance under those lights. — Muhammad Ali");
        quotes.add("Most of the important things in the world have been accomplished by people who have kept on trying when there seemed to be no hope at all. — Dale Carnegie");
        quotes.add("The only place where success comes before work is in the dictionary. — Vince Lombardi");
        quotes.add("There are no secrets to success. It is the result of preparation, hard work, and learning from failure. — Colin Powell");
        quotes.add("Strive not to be a success, but rather to be of value. — Albert Einstein");
        quotes.add("Try not to become a man of success. Rather become a man of value. — Albert Einstein");
        quotes.add("Success is stumbling from failure to failure with no loss of enthusiasm. — Winston S. Churchill");
        quotes.add("Don't spend time beating on a wall, hoping to transform it into a door. — Coco Chanel");
        quotes.add("If A is a success in life, then A equals x plus y plus z. Work is x; y is play; and z is keeping your mouth shut. — Albert Einstein");
        quotes.add("It is hard to fail, but it is worse never to have tried to succeed. — Theodore Roosevelt");
        quotes.add("If you want to live a happy life, tie it to a goal, not to people or things. — Albert Einstein");

        displayRandomQuote();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote(currentQuote);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFavoriteQuote(currentQuote);
            }
        });

        viewFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFavoriteQuotes();
            }
        });

        scheduleDailyQuoteRefresh();
    }

    private void displayRandomQuote() {
        Random random = new Random();
        currentQuote = quotes.get(random.nextInt(quotes.size()));
        quoteTextView.setText(currentQuote);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_QUOTE_KEY, currentQuote);
        editor.apply();
    }

    private void shareQuote(String quote) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, quote);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void saveFavoriteQuote(String quote) {
        List<String> favoriteQuotes = getFavoriteQuotes();
        if (!favoriteQuotes.contains(quote)) {
            favoriteQuotes.add(quote);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteQuotes);
        editor.putString(FAVORITE_QUOTES_KEY, json);
        editor.apply();
    }

    private List<String> getFavoriteQuotes() {
        String json = sharedPreferences.getString(FAVORITE_QUOTES_KEY, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> favoriteQuotes = gson.fromJson(json, type);
        return favoriteQuotes != null ? favoriteQuotes : new ArrayList<String>();
    }

    private void viewFavoriteQuotes() {
        Intent intent = new Intent(MainActivity.this, FavoriteQuotesActivity.class);
        startActivity(intent);
    }

    private void scheduleDailyQuoteRefresh() {
        Intent intent = new Intent(this, QuoteRefreshReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

}
