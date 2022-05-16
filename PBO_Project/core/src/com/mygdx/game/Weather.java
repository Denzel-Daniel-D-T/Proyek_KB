package com.mygdx.game;

import java.util.Arrays;
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
    private static boolean[] isRuleActive = new boolean[20];

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
        Arrays.fill(isRuleActive, true);
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
            Arrays.fill(isRuleActive, true);

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
        if (prevPrecipitation == Precipitation.NONE && (change == Change.NONE || change == Change.LOWER) && isRuleActive[0]) {
            precipitation = Precipitation.NONE;
            isRuleActive[0] = false;
            System.out.println("R1");
            return false;
        }
        if (prevPrecipitation == Precipitation.NONE && change == Change.HIGHER && isRuleActive[1]) {
            precipitation = Precipitation.LOW;
            isRuleActive[1] = false;
            System.out.println("R2");
            return false;
        }
        if (prevPrecipitation == Precipitation.LOW && change == Change.NONE && isRuleActive[2]) {
            precipitation = Precipitation.LOW;
            isRuleActive[2] = false;
            System.out.println("R3");
            return false;
        }
        if (prevPrecipitation == Precipitation.LOW && change == Change.LOWER && isRuleActive[3]) {
            precipitation = Precipitation.NONE;
            isRuleActive[3] = false;
            System.out.println("R4");
            return false;
        }
        if (prevPrecipitation == Precipitation.LOW && change == Change.HIGHER && isRuleActive[4]) {
            precipitation = Precipitation.HIGH;
            isRuleActive[4] = false;
            System.out.println("R5");
            return false;
        }
        if (prevPrecipitation == Precipitation.HIGH && (change == Change.NONE || change == Change.HIGHER) && isRuleActive[5]) {
            precipitation = Precipitation.HIGH;
            isRuleActive[5] = false;
            System.out.println("R6");
            return false;
        }
        if (prevPrecipitation == Precipitation.HIGH && change == Change.LOWER && isRuleActive[6]) {
            precipitation = Precipitation.LOW;
            isRuleActive[6] = false;
            System.out.println("R7");
            return false;
        }
        if (prevCloud > cloud && Math.abs(prevCloud - cloud) >= 0.1f && isRuleActive[7]) {
            change = Change.LOWER;
            isRuleActive[7] = false;
            System.out.println("R8");
            return false;
        }
        if (Math.abs(prevCloud - cloud) < 0.1f && isRuleActive[8]) {
            change = Change.NONE;
            isRuleActive[8] = false;
            System.out.println("R9");
            return false;
        }
        if (prevCloud < cloud && Math.abs(prevCloud - cloud) >= 0.1f && isRuleActive[9]) {
            change = Change.HIGHER;
            isRuleActive[9] = false;
            System.out.println("R10");
            return false;
        }
        if (precipitation == Precipitation.NONE && season == Season.DRY && isRuleActive[10]) {
            rain = Rain.NONE;
            isRuleActive[10] = false;
            System.out.println("R11");
            return false;
        }
        if (precipitation == Precipitation.NONE && season == Season.WET && cloud >= 0.75f && isRuleActive[11]) {
            rain = Rain.LIGHT;
            isRuleActive[11] = false;
            System.out.println("R12");
            return false;
        }
        if (precipitation == Precipitation.NONE && season == Season.WET && cloud < 0.75f && isRuleActive[12]) {
            rain = Rain.NONE;
            isRuleActive[12] = false;
            System.out.println("R13");
            return false;
        }
        if (precipitation == Precipitation.LOW && season == Season.DRY && cloud >= 0.5f && isRuleActive[13]) {
            rain = Rain.LIGHT;
            isRuleActive[13] = false;
            System.out.println("R14");
            return false;
        }
        if (precipitation == Precipitation.LOW && season == Season.DRY && cloud < 0.5f && isRuleActive[14]) {
            rain = Rain.NONE;
            isRuleActive[14] = false;
            System.out.println("R15");
            return false;
        }
        if (precipitation == Precipitation.LOW && season == Season.WET && cloud >= 0.95f && isRuleActive[15]) {
            rain = Rain.HEAVY;
            isRuleActive[15] = false;
            System.out.println("R16");
            return false;
        }
        if (precipitation == Precipitation.LOW && season == Season.WET && cloud >= 0.4f && cloud < 0.95f && isRuleActive[16]) {
            rain = Rain.LIGHT;
            isRuleActive[16] = false;
            System.out.println("R17");
            return false;
        }
        if (precipitation == Precipitation.LOW && season == Season.WET && cloud < 0.4f && isRuleActive[17]) {
            rain = Rain.NONE;
            isRuleActive[17] = false;
            System.out.println("R18");
            return false;
        }
        if (precipitation == Precipitation.HIGH && cloud >= 0.1f && isRuleActive[18]) {
            rain = Rain.HEAVY;
            isRuleActive[18] = false;
            System.out.println("R19");
            return false;
        }
        if (precipitation == Precipitation.HIGH && cloud < 0.1f && isRuleActive[19]) {
            rain = Rain.LIGHT;
            isRuleActive[19] = false;
            System.out.println("R20");
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

    public static float getPrevCloud() {
        return prevCloud;
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
