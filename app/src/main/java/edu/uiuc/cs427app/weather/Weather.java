package edu.uiuc.cs427app.weather;


import java.util.ArrayList;
import java.util.List;

public class Weather {
    public Double temp_c;
    public Double temp_f;
    public Double feelslike_c;
    public Double feelslike_f;
    public Integer humidity;
    public Integer cloud;
    public Double uv;
    public Wind wind;
    public Condition condition;
    public String local_time;

    /*
    * This is the class for information about weather conditions.
    */
    public static class Condition {
        public String text;
        public String icon;
        public String code;

        public Condition(String text, String icon, String code) {
            this.text = text;
            this.icon = icon;
            this.code = code;
        }
    }

    /*
    * This is the class for information about the wind. 
    */
    public static class Wind {
        public Double wind_mph;
        public Integer wind_degree;
        public String wind_dir;

        public Wind(Double wind_mph, Integer wind_degree, String wind_dir) {
            this.wind_mph = wind_mph;
            this.wind_degree = wind_degree;
            this.wind_dir = wind_dir;
        }
    }

    /*
    * This method updates the weather condition.
    */
    public void setCondition(String text, String icon, String code) {
        this.condition = new Condition(text, icon, code);
    }

}
