package com.mygdx.game;

import java.util.Random;

public class Weather {
    public enum Season {
        DRY,
        WET
    }

    public enum Change {
        UNKNOWN,
        NONE,
        LOWER,
        HIGHER
    }

    public enum Precipitation {
        UNKNOWN,
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
    private static boolean firstRun = true;
    private static float playTime;

    //Calculated data members
    private static float windStrength;
    private static float windDirection;
    private static Rain rain = Rain.UNKNOWN;
    private static Precipitation precipitation = Precipitation.UNKNOWN;
    private static Change change = Change.UNKNOWN;

    //Input data members
    private static Season season;
    private static Precipitation prevPrecipitation;
    private static float cloud;
    private static float prevCloud;

    public static void resetWeather() {
        rain = Rain.UNKNOWN;
        if (firstRun) {
            prevCloud = random.nextFloat();
        }
        else {
            prevPrecipitation = precipitation;
            prevCloud = cloud;
        }
        precipitation = Precipitation.UNKNOWN;
    }

    public static void setWeather(Season season) {
        if (firstRun) {
            Weather.season = season;

            //For now random, later tied to difficulty
            prevPrecipitation = Weather.Precipitation.class.getEnumConstants()[random.nextInt(Precipitation.class.getEnumConstants().length - 1) + 1];
            //========================================

            cloud = random.nextFloat();

            windStrength = random.nextFloat();
            windDirection = random.nextFloat() * 360;
            firstRun = false;
        }
        else {
            cloud += -0.3f + random.nextFloat() * 0.6f;
            cloud = Math.max(0, cloud);
            cloud = Math.min(cloud, 1.0f);

            windStrength += -0.2f + random.nextFloat() * 0.4f;
            windStrength = Math.max(0 + playTime / 240f, windStrength);
            windStrength = Math.min(windStrength, 1.0f + playTime / 240f);

            windDirection += -45 + random.nextFloat() * 90;
            if (windDirection < 0) {
                windDirection = 360 + windDirection;
            }
            else if (windDirection > 360) {
                windDirection -= 360;
            }
        }
    }

    public static boolean calculateWeather() {
        if (rain == Rain.UNKNOWN) {
            if (prevPrecipitation == Precipitation.NONE && (change == Change.NONE || change == Change.LOWER)) {
                precipitation = Precipitation.NONE;
                
            }
            if (prevPrecipitation == Precipitation.NONE && change == Change.HIGHER) {
                precipitation = Precipitation.LOW;
                
            }
            if (prevPrecipitation == Precipitation.LOW && change == Change.NONE) {
                precipitation = Precipitation.LOW;
                
            }
            if (prevPrecipitation == Precipitation.LOW && change == Change.LOWER) {
                precipitation = Precipitation.NONE;
                
            }
            if (prevPrecipitation == Precipitation.LOW && change == Change.HIGHER) {
                precipitation = Precipitation.HIGH;
                
            }
            if (prevPrecipitation == Precipitation.HIGH && change == Change.HIGHER) {
                precipitation = Precipitation.HIGH;
                
            }
            if (prevPrecipitation == Precipitation.HIGH && (change == Change.NONE || change == Change.LOWER)) {
                precipitation = Precipitation.HIGH;
                
            }
            if (prevCloud > cloud && Math.abs(prevCloud - cloud) >= 0.1f) {
                change = Change.LOWER;
                
            }
            if (Math.abs(prevCloud - cloud) < 0.1f) {
                change = Change.NONE;
                
            }
            if (prevCloud < cloud && Math.abs(prevCloud - cloud) >= 0.1f) {
                change = Change.HIGHER;
                
            }
            if (precipitation == Precipitation.NONE && season == Season.DRY) {
                rain = Rain.NONE;
                
            }
            if (precipitation == Precipitation.NONE && season == Season.WET && cloud >= 0.75f) {
                rain = Rain.LIGHT;
                
            }
            if (precipitation == Precipitation.NONE && season == Season.WET && cloud < 0.75f) {
                rain = Rain.NONE;
                
            }
            if (precipitation == Precipitation.LOW && season == Season.DRY && cloud >= 0.5f) {
                rain = Rain.LIGHT;
                
            }
            if (precipitation == Precipitation.LOW && season == Season.DRY && cloud < 0.5f) {
                rain = Rain.NONE;
                
            }
            if (precipitation == Precipitation.LOW && season == Season.WET && cloud >= 0.95f) {
                rain = Rain.HEAVY;
                
            }
            if (precipitation == Precipitation.LOW && season == Season.WET && cloud >= 0.4f && cloud < 0.95f) {
                rain = Rain.LIGHT;
                
            }
            if (precipitation == Precipitation.LOW && season == Season.WET && cloud < 0.4f) {
                rain = Rain.NONE;
                
            }
            if (precipitation == Precipitation.HIGH && cloud >= 0.1f) {
                rain = Rain.HEAVY;
                
            }
            if (precipitation == Precipitation.HIGH && cloud < 0.1f) {
                rain = Rain.LIGHT;
                
            }
            return false;
        }

        return true;
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

    public static float getCloud() {
        return cloud;
    }

    public static Precipitation getPrevPrecipitation() {
        return prevPrecipitation;
    }

    public static Change getChange() {
        return change;
    }

    public static void setPlayTime(float playTime) {
        Weather.playTime = playTime;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }
}
