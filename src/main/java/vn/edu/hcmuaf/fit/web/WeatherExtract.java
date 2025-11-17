package vn.edu.hcmuaf.fit.web;

import org.json.*;
import vn.edu.hcmuaf.fit.web.ControlManager.ConfigManager;
import vn.edu.hcmuaf.fit.web.ControlManager.ETLRunner;

import java.io.*;
import java.net.*;

public class WeatherExtract {
    private final ConfigManager config;

    public WeatherExtract(ConfigManager config) {
        this.config = config;
    }

    public void run() {
        try {
            String apiKey = config.getApiKey();
            String baseUrl = config.getApiUrl();
            String[] cities = config.getCities();
            int days = config.getDays();
            String aqi = config.getAqi();
            String alerts = config.getAlerts();


            FileWriter csvWriter = new FileWriter("weather_raw.csv");
            csvWriter.append("city_name, country,timezone,full_date,max_temp,min_temp,avg_temp,avg_humidity,maxwind_kph,uv,rain_chance,condition_text\n");

            for (String city : cities) {
                String apiUrl = String.format(
                        "%s?key=%s&q=%s&days=%d&aqi=%s&alerts=%s",
                        baseUrl, apiKey, city, days, aqi, alerts
                );

                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject location = json.getJSONObject("location");
                JSONArray forecastDays = json.getJSONObject("forecast").getJSONArray("forecastday");

                for (int j = 0; j < forecastDays.length(); j++) {
                    JSONObject day = forecastDays.getJSONObject(j);
                    JSONObject dayInfo = day.getJSONObject("day");
                    csvWriter.append(String.format(
                            "%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s\n",
                            location.getString("name"),
                            location.optString("country"),
                            location.optString("tz_id", ""),
                            day.getString("date"),
                            dayInfo.getDouble("maxtemp_c"),
                            dayInfo.getDouble("mintemp_c"),
                            dayInfo.getDouble("avgtemp_c"),
                            dayInfo.getDouble("avghumidity"),
                            dayInfo.optDouble("maxwind_kph"),
                            dayInfo.optDouble("uv"),
                            dayInfo.optDouble("daily_chance_of_rain", 0.0),
                            dayInfo.getJSONObject("condition").getString("text").replace(",", " ")
                    ));
                }
            }
            csvWriter.close();
            System.out.println("Extract thành công -> weather_raw.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Chạy thủ công
    public static void main(String[] args) {
        final String STEP_NAME = "EXTRACT";
        ConfigManager config = new ConfigManager("config.xml");
        ETLRunner.runAndLog(
                STEP_NAME,
                () -> new WeatherExtract(config).run() //
        );
    }
}
