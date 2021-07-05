package nudge.graphics;

import nudge.util.U;

/**
 * @author Frederik Dahl
 * XX/XX/2020
 */

public class Color implements Comparable<Color>{

    // todo: re-evaluate. priority: low

    private static final float INV = 0.003921569f; // (1 / 255)

    public static final Color WHITE =       new Color(255, 255, 255, 255);
    public static final Color BLACK =       new Color(0, 0, 0, 255);
    public static final Color RED =         new Color(255, 0, 0, 255);
    public static final Color DARK_RED =    new Color(127, 0, 0, 255);
    public static final Color GREEN =       new Color(0, 255, 0, 255);
    public static final Color BLUE =        new Color(0, 0, 255, 255);
    public static final Color DARK_BLUE =   new Color(0, 0, 127, 255);
    public static final Color DIRTY_BLUE =  new Color(0, 127, 127, 255);
    public static final Color PINK =        new Color(255, 0, 255, 255);
    public static final Color CYAN =        new Color(0, 255, 255, 255);
    public static final Color YELLOW =      new Color(255, 255, 0, 255);
    public static final Color PURPLE =      new Color(127, 0, 127, 255);


    private int r, g, b, a;
    private float x, y, z, w;
    private float floatBits;

    private Color() { }

    private Color(int r, int g, int b, int a) {
        this.r = U.clamp(r,0,255);
        this.g = U.clamp(g,0,255);
        this.b = U.clamp(b,0,255);
        this.a = U.clamp(a,0,255);
        x = r * INV;
        y = g * INV;
        z = b * INV;
        w = a * INV;
        setFloatBits();
    }

    private Color(float x, float y, float z, float w) {
        this.x = U.clamp(x,0f,1f);
        this.y = U.clamp(y,0f,1f);
        this.z = U.clamp(z,0f,1f);
        this.w = U.clamp(w,0f,1f);
        r = (int)(x * 255);
        g = (int)(y * 255);
        b = (int)(z * 255);
        a = (int)(w * 255);
        setFloatBits();
    }

    public float r() { return x; }

    public float g() { return y; }

    public float b() { return z; }

    public float a() { return w; }

    public int r255() { return r; }

    public int g255() { return g; }

    public int b255() { return b; }

    public int a255() { return a; }

    public void setR(int r255) {
        r = U.clamp(r255,0,255);
        x = r * INV;
        setFloatBits();
    }

    public void setG(int g255) {
        g = U.clamp(g255,0,255);
        y = g * INV;
        setFloatBits();
    }

    public void setB(int b255) {
        b = U.clamp(b255,0,255);
        z = b * INV;
        setFloatBits();
    }

    public void setA(int a255) {
        a = U.clamp(a255,0,255);
        w = a * INV;
        setFloatBits();
    }

    public void setR(float r) {
        x = U.clamp(r,0f,1f);
        this.r = (int)(x * 255);
        setFloatBits();
    }

    public void setG(float g) {
        y = U.clamp(g,0f,1f);
        this.g = (int)(y * 255);
        setFloatBits();
    }

    public void setB(float b) {
        z = U.clamp(b,0f,1f);
        this.b = (int)(z * 255);
        setFloatBits();
    }

    public void setA(float a) {
        w = U.clamp(a,0f,1f);
        this.a = (int)(w * 255);
        setFloatBits();
    }

    public Color set(Color color) {

        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;
        x = color.x;
        y = color.y;
        z = color.z;
        w = color.w;

        setFloatBits();

        return this;
    }

    public void set(int r, int g, int b, int a) {
        this.r = U.clamp(r,0,255);
        this.g = U.clamp(g,0,255);
        this.b = U.clamp(b,0,255);
        this.a = U.clamp(a,0,255);
        x = this.r * INV;
        y = this.g * INV;
        z = this.b * INV;
        w = this.a * INV;
        setFloatBits();
    }

    public void set(float r, float g, float b, float a) {
        x = U.clamp(r,0f,1f);
        y = U.clamp(g,0f,1f);
        z = U.clamp(b,0f,1f);
        w = U.clamp(a,0f,1f);
        this.r = (int)(x * 255);
        this.g = (int)(y * 255);
        this.b = (int)(z * 255);
        this.a = (int)(w * 255);
        setFloatBits();
    }

    public void set(int intBits) {
        set(
                (intBits & 0xff000000) >>> 24,
                (intBits & 0x00ff0000) >>> 16,
                (intBits & 0x0000ff00) >>> 8,
                (intBits & 0x000000ff)
        );
    }

    public Color multiply(Color color) {
        x *= color.x;
        y *= color.y;
        z *= color.z;
        w *= color.w;
        r = (int)(x * 255);
        g = (int)(y * 255);
        b = (int)(z * 255);
        a = (int)(w * 255);
        setFloatBits();
        return this;
    }

    public Color mix(Color color) {
        x = (x + color.x) * 0.5f;
        y = (y + color.y) * 0.5f;
        z = (z + color.z) * 0.5f;
        w = (w + color.w) * 0.5f;
        r = (int)(x * 255);
        g = (int)(y * 255);
        b = (int)(z * 255);
        a = (int)(w * 255);
        setFloatBits();
        return this;
    }

    public Color mix(Color color, float influence) {
        if (influence <= 0) {
            return this;
        }
        else if (influence >= 1) {
            return this.set(color);
        }
        x = color.x * influence + x * (1 - influence);
        y = color.y * influence + y * (1 - influence);
        z = color.z * influence + z * (1 - influence);
        w = color.w * influence + w * (1 - influence);
        r = (int)(x * 255);
        g = (int)(y * 255);
        b = (int)(z * 255);
        a = (int)(w * 255);
        setFloatBits();
        return this;

    }

    public Color copy() {
        return copy(this);
    }

    public int rgba8888 () {
        return r << 24 | g << 16 | b << 8 | a;
    }


    /* Encodes the ABGR int color as a float. The alpha is compressed to use only even numbers between 0-254 to avoid using bits
     * in the NaN range (see {@link Float#intBitsToFloat(int)} javadocs). Rendering which uses colors encoded as floats should
     * expand the 0-254 back to 0-255, else colors cannot be fully opaque. */

    private void setFloatBits() {
        int color = a << 24 | b << 16 | g << 8 | r;
        floatBits = Float.intBitsToFloat(color & 0xfeffffff);
    }

    public float floatBits() {
        return floatBits;
    }


    public String hexString () {
        String value = Integer.toHexString(rgba8888());
        while (value.length() < 8) value = "0" + value;
        return value;
    }


    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color)o;
        return compareTo(color) == 0;
    }

    @Override
    public int hashCode () {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        result = 31 * result + (w != +0.0f ? Float.floatToIntBits(w) : 0);
        return result;
    }

    @Override
    public int compareTo(Color o) {
        return Integer.compare(o.rgba8888(),this.rgba8888());
    }


    public static Color copy(Color c) {
        return new Color(c.r,c.g,c.b,c.a);
    }

    public static Color random() {

        return new Color(
                U.rndFloat(),
                U.rndFloat(),
                U.rndFloat(),
                1.0f);
    }

    public static Color fromNormalized(float r, float g, float b, float a) {
        return new Color(r,g,b,a);
    }

    public static Color fromRGBA(int r, int g, int b, int a) {
        return new Color(r,g,b,a);
    }

    public static Color fromHex(String hex) {

        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;

        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16),
                hex.length() != 8 ? 255 : Integer.parseInt(hex.substring(6, 8), 16)
        );
    }


}
