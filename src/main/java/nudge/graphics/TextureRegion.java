package nudge.graphics;

/**
 * @author Frederik Dahl
 * 29/06/2021
 */

public class TextureRegion {

    private  int w;
    private  int h;

    private float u;
    private float v;
    private float u2;
    private float v2;

    private Texture texture;


    public TextureRegion (Texture texture) {

        if (texture == null)

            throw new IllegalArgumentException("texture cannot be null.");

        this.texture = texture;

        setRegion(0, 0, texture.width(), texture.height());
    }

    public TextureRegion (Texture texture, int width, int height) {

        this.texture = texture;

        setRegion(0, 0, width, height);
    }

    public TextureRegion (Texture texture, int x, int y, int width, int height) {

        this.texture = texture;

        setRegion(x, y, width, height);
    }

    public TextureRegion (Texture texture, float u, float v, float u2, float v2) {

        this.texture = texture;

        setRegion(u, v, u2, v2);
    }

    public void setRegion (Texture texture) {

        this.texture = texture;

        setRegion(0, 0, texture.width(), texture.height());
    }

    public void setRegion (int x, int y, int width, int height) {

        float invTexWidth = 1f / texture.width();
        float invTexHeight = 1f / texture.height();

        setRegion(
                x * invTexWidth,
                y * invTexHeight,
                (x + width) * invTexWidth,
                (y + height) * invTexHeight);

        w = Math.abs(width);
        h = Math.abs(height);
    }

    public void setRegion (float u, float v, float u2, float v2) {

        int texWidth = texture.width();
        int texHeight = texture.height();

        w = Math.round(Math.abs(u2 - u) * texWidth);
        h = Math.round(Math.abs(v2 - v) * texHeight);

        if (w == 1 && h == 1) {

            float adjustX = 0.25f / texWidth;
            u += adjustX;
            u2 -= adjustX;
            float adjustY = 0.25f / texHeight;
            v += adjustY;
            v2 -= adjustY;
        }

        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }

    public void setRegion (TextureRegion region) {

        texture = region.texture;

        setRegion(region.u, region.v, region.u2, region.v2);
    }

    public void setRegion (TextureRegion region, int x, int y, int width, int height) {

        texture = region.texture;

        setRegion(region.x() + x, region.y() + y, width, height);
    }

    public int x() {
        return Math.round(u * texture.width());
    }

    public int y() {
        return Math.round(v * texture.height());
    }

    public int w() { return w; }

    public int h() {
        return h;
    }

    public float u() {
        return u;
    }

    public float v() {
        return v;
    }

    public float u2() {
        return u2;
    }

    public float v2() {
        return v2;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setX (int x) {
        setU(x / (float)texture.width());
    }

    public void setY (int y) {
        setV(y / (float)texture.height());
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setU(float u) {

        this.u = u;

        w = Math.round(Math.abs(u2 - u) * texture.width());
    }

    public void setV(float v) {

        this.v = v;

        h = Math.round(Math.abs(v2 - v) * texture.height());
    }

    public void setU2(float u2) {

        this.u2 = u2;

        w = Math.round(Math.abs(u2 - u) * texture.width());
    }

    public void setV2(float v2) {

        this.v2 = v2;

        h = Math.round(Math.abs(v2 - v) * texture.height());
    }

    public Texture getTexture () {
        return texture;
    }

    public void setTexture (Texture texture) {
        this.texture = texture;
    }

    public void flip (boolean x, boolean y) {

        if (x) {
            float temp = u;
            u = u2;
            u2 = temp;
        }
        if (y) {
            float temp = v;
            v = v2;
            v2 = temp;
        }
    }

    public boolean isFlipX () {
        return u > u2;
    }

    public boolean isFlipY () {
        return v > v2;
    }

    public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight, boolean bleedFix) {

        return new TextureRegion(texture).split(tileWidth,tileHeight,bleedFix);
    }

    public TextureRegion[][] split(int tileWidth, int tileHeight) {
        return split(tileWidth,tileHeight,false);
    }

    public TextureRegion[] split(int tileWidth, int tileHeight, int count, int offset, boolean bleedFix) {

        final int rows = h / tileHeight;
        final int cols = w / tileWidth;

        final float invWidth = 1f / w;
        final float invHeight = 1f / h;
        final float fix = bleedFix ? 0.001f : 0;

        int x = x();
        int y = y();

        int pointer = 0;
        int startX = x;

        TextureRegion[] result = new TextureRegion[count];

        out:

        for (int row = 0; row < rows; row++, y += tileHeight, x = startX) {

            for (int col = 0; col < cols; col++, x += tileWidth) {

                if (offset > 0) offset--;

                else { if (count-- == 0) break out;

                    float u =  (x + fix) * invWidth;
                    float u2 = (x + tileWidth  - fix) * invWidth;
                    float v  = (y + fix) * invHeight;
                    float v2 = (y + tileHeight - fix) * invHeight;

                    result[pointer++] = new TextureRegion(texture,u,v,u2,v2);
                }
            }
        }
        return result;
    }

    public TextureRegion[][] split(int tileWidth, int tileHeight, boolean bleedFix) {

        final int rows = h / tileHeight;
        final int cols = w / tileWidth;

        int x = x();
        int y = y();

        int startX = x;

        TextureRegion[][] tiles = new TextureRegion[rows][cols];

        for (int row = 0; row < rows; row++, y += tileHeight, x = startX) {

            for (int col = 0; col < cols; col++, x += tileWidth) {

                TextureRegion region;

                if (bleedFix) {

                    float fix = 0.0001f;
                    float invWidth = 1f / w;
                    float invHeight = 1f / h;

                    float u =  (x + fix) * invWidth;
                    float u2 = (x + tileWidth  - fix) * invWidth;
                    float v  = (y + fix) * invHeight;
                    float v2 = (y + tileHeight - fix) * invHeight;

                    region = new TextureRegion(texture,u,v,u2,v2);
                }
                else region = new TextureRegion(texture, x, y, tileWidth, tileHeight);

                tiles[row][col] = region;
            }
        }
        return tiles;
    }

}
