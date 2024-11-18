package com.example.campusexpensemanager.Helper;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyService {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static final String API_KEY = "f96651fb294470efbaf9252d";
    public interface ExchangeRateListener {
        void onExchangeRateReceived(JSONObject exchangeRates);
        void onError(String error);
    }

    public static void fetchExchangeRates(Context context, ExchangeRateListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(API_URL + "?apikey=" + API_KEY);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        listener.onExchangeRateReceived(jsonObject.getJSONObject("rates"));
                    } catch (Exception e) {
                        listener.onError("Error parsing data");
                    }
                } else {
                    listener.onError("Failed to fetch exchange rates");
                }
            }
        }.execute();
    }
}
