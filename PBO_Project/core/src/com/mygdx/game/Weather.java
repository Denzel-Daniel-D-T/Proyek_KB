package com.mygdx.game;

import java.util.Random;

public class Weather {
    public enum Season {
        DRY,
        WET
    }

    public enum Precipitation {
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

    //Input data members
    private static Season season;
    private static Precipitation precipitation;
    private static float cloud;

    public static void resetWeather() {
        rain = Rain.UNKNOWN;
    }

    public static void setWeather(Season season) {
        if (firstRun) {
            Weather.season = season;
            precipitation = Weather.randomEnum(Weather.Precipitation.class);
            cloud = random.nextFloat();

            windStrength = random.nextFloat();
            windDirection = random.nextFloat() * 360;
            firstRun = false;
        }
        else {
            int x;
            switch (precipitation) {
                case NONE:
                    x = random.nextInt(2);
                    if (x == 0) {
                        precipitation = Precipitation.NONE;
                    }
                    else {
                        precipitation = Precipitation.LOW;
                    }
                    break;
                case LOW:
                    precipitation = Weather.randomEnum(Weather.Precipitation.class);
                    break;
                case HIGH:
                    x = random.nextInt(2);
                    if (x == 0) {
                        precipitation = Precipitation.LOW;
                    }
                    else {
                        precipitation = Precipitation.HIGH;
                    }
                    break;
            }
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
            if (precipitation == Precipitation.NONE && season == Season.DRY) {
                rain = Rain.NONE;
            }
            if (precipitation == Precipitation.NONE && season == Season.WET) {
                if (cloud >= 0.75f) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.NONE;
                }
            }
            if (precipitation == Precipitation.LOW && season == Season.DRY) {
                if (cloud > 0.5f) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.NONE;
                }
            }
            if (precipitation == Precipitation.LOW && season == Season.WET) {
                if (cloud >= 0.95f) {
                    rain = Rain.HEAVY;
                }
                else if (cloud >= 0.4f) {
                    rain = Rain.LIGHT;
                }
                else {
                    rain = Rain.NONE;
                }
            }
            if (precipitation == Precipitation.HIGH) {
                if (cloud >= 0.1f) {
                    rain = Rain.HEAVY;
                }
                else {
                    rain = Rain.LIGHT;
                }
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

    public static void setPlayTime(float playTime) {
        Weather.playTime = playTime;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }
}
