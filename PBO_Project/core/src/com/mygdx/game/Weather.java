package com.mygdx.game;

import java.util.Random;

public class Weather {
    private enum Season {
        DRY,
        WET
    }

    private enum Precipitation {
        NONE,
        LOW,
        HIGH
    }

    private enum Rain {
        UNKNOWN,
        NONE,
        LIGHT,
        HEAVY
    }
    private static final Random random = new Random();

    //Calculated data members
    private static float windStrength;
    private static float windDirection;
    private static Rain rain;

    //Input data members
    private static Season season;
    private static Precipitation precipitation;

    public static void resetWeather() {
        rain = Rain.UNKNOWN;
    }

    public static void setWeather(Season season, Precipitation precipitation) {
        Weather.season = season;
        Weather.precipitation = precipitation;
    }

    public static boolean calculateWeather() {
        if (rain == Rain.UNKNOWN) {
            if (precipitation == Precipitation.NONE && season == Season.DRY) {
                rain = Rain.NONE;
                return true;
            }
            if (precipitation == Precipitation.NONE && season == Season.WET) {
                int randomNumber = random.nextInt(100);
                if (randomNumber < 25) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.NONE;
                }
                return true;
            }
            if (precipitation == Precipitation.LOW && season == Season.DRY) {
                int randomNumber = random.nextInt(100);
                if (randomNumber < 50) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.NONE;
                }
                return true;
            }
            if (precipitation == Precipitation.LOW && season == Season.WET) {
                int randomNumber = random.nextInt(100);
                if (randomNumber < 60) {
                    rain = Rain.LIGHT;
                }
                else if (randomNumber >= 95 && season == Season.WET) {
                    rain = Rain.HEAVY;
                }
                else {
                    rain = Rain.NONE;
                }
            }
            if (precipitation == Precipitation.HIGH) {
                int randomNumber = random.nextInt(100);
                if (randomNumber < 10) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.HEAVY;
                }
                return true;
            }
        }

        return false;
    }

    public static float getWindStrength() {
        return windStrength;
    }

    public static float getWindDirection() {
        return windDirection;
    }

    public static Rain getRain() {
        return rain;
    }

    public static Season getSeason() {
        return season;
    }

    public static Precipitation getPrecipitation() {
        return precipitation;
    }
}
