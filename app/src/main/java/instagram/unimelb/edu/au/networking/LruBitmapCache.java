package instagram.unimelb.edu.au.networking;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Caches the images
 */
public class LruBitmapCache extends LruCache<String,Bitmap> implements ImageCache{

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String s) {
        return null;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {

    }
}
