package nudge.util;


import org.joml.Vector2f;

import java.util.Random;

/**
 * @author Frederik Dahl
 * XX/XX/2020
 */

public class U {

    private static final Random RND = new Random();

    private static final int[] logTable = new int[256];

    static
    {
        logTable[0] = logTable[1] = 0;
        for (int i=2; i<256; i++) logTable[i] = 1 + logTable[i/2];
        logTable[0] = -1;
    }

    public static final int[][] adjacentArr = {

            {-1, 1},{ 0, 1},{ 1, 1},
            {-1, 0},        { 1, 0},
            {-1,-1},{ 0,-1},{ 1,-1}

    };

    private U() {}

    public static float map(float value, float b1, float e1, float b2, float e2) {
        return b2 + (e2 - b2) * ((value - b1) / (e1 - b1));
    }

    public static float mapFromNormalized(float floatValue, float b, float e) {
        return b + (e - b) * floatValue;
    }

    public static float dist(Vector2f v1, Vector2f v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dist2(Vector2f v1, Vector2f v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        return dx * dx + dy * dy;
    }

    public static float dist2(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public static int log2(float f) {
        int x = Float.floatToIntBits(f);
        int c = x >> 23;

        if (c != 0) return c - 127; //Compute directly from exponent.
        else //Subnormal, must compute from mantissa.
        {
            int t = x >> 16;
            if (t != 0) return logTable[t] - 133;
            else return (x >> 8 != 0) ? logTable[t] - 141 : logTable[x] - 149;
        }
    }

    public static void rotate(Vector2f dest, Vector2f origin, float angleDeg) {

        float x = dest.x - origin.x;
        float y = dest.y - origin.y;
        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = x * cos - y * sin;
        float yPrime = x * sin + y * cos;

        dest.set(xPrime + origin.x, yPrime + origin.y);
    }

    public static boolean compare(float f1, float f2, float epsilon) {
        return Math.abs(f1-f2) <= epsilon * Math.max(1.0f, Math.max(Math.abs(f1),Math.abs(f2)));
    }

    public static boolean compare(Vector2f v1, Vector2f v2, float epsilon) {
        return compare(v1.x, v2.x, epsilon) && compare(v1.y, v2.y, epsilon);
    }

    public static boolean compare(Vector2f v1, Vector2f v2) {
        return compare(v1.x, v2.x, Float.MIN_VALUE) && compare(v1.y, v2.y, Float.MIN_VALUE);
    }

    public static byte setFlag(byte flag, byte flags) {
        return (byte) (flag | flags);
    }

    public static short setFlag(short flag, short flags) {
        return (short) (flag | flags);
    }

    public static int setFlag(int flag, int flags) {
        return (flag | flags);
    }

    public static long setFlag(long flag, long flags) {
        return (flag | flags);
    }

    public static boolean flagIsSet(byte flag, byte flags) {
        return (flag & flags) != 0;
    }

    public static boolean flagIsSet(short flag, short flags) {
        return (flag & flags) != 0;
    }

    public static boolean flagIsSet(int flag, int flags) {
        return (flag & flags) != 0;
    }

    public static boolean flagIsSet(long flag, long flags) {
        return (flag & flags) != 0;
    }

    public static float round(float x) {
        return Math.round(x);
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clamp(float value, float min, float max) {
        return (value > max) ? max : (Math.max(value, min));
    }

    public static boolean rndBool() {
        return RND.nextBoolean();
    }

    public static int rndInt(int min, int max) {
        return  RND.nextInt((max - min) + 1) + min;
    }

    public static float rndFloat() {
        return RND.nextFloat();
    }

    public static double rndDouble() {
        return RND.nextDouble();
    }

    public static int rndPoisson(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * RND.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }


}
