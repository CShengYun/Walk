package com.txzh.walk.ToolClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.Toast;

import com.loopj.android.image.WebImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteCaChe {
    private static final String DISK_CACHE_PATH = "/web_image_cache/";
    private ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache = new ConcurrentHashMap();
    private String diskCachePath;
    private boolean diskCacheEnabled = false;
    private ExecutorService writeThread;
    private Context c;
    private Handler h;

    public DeleteCaChe(Context var1 ,Handler h) {
        this.h = h;
        c = var1;
        Context var2 = var1.getApplicationContext();
        this.diskCachePath = var2.getCacheDir().getAbsolutePath() + "/web_image_cache/";
        File var3 = new File(this.diskCachePath);
        //var3.mkdirs();
        this.diskCacheEnabled = var3.exists();
        this.writeThread = Executors.newSingleThreadExecutor();
    }

    public Bitmap get(String var1) {
        Bitmap var2 = null;
        var2 = this.getBitmapFromMemory(var1);
        if (var2 == null) {
            var2 = this.getBitmapFromDisk(var1);
            if (var2 != null) {
                this.cacheBitmapToMemory(var1, var2);
            }
        }

        return var2;
    }

    public void put(String var1, Bitmap var2) {
        this.cacheBitmapToMemory(var1, var2);
        this.cacheBitmapToDisk(var1, var2);
    }

    public void remove(String var1) {
        WebImage.removeFromCache(var1);
        if (var1 != null) {
            File var2 = new File(this.diskCachePath, this.getCacheKey(var1));
            if(var2.exists() && var2.isFile()) {
                var2.delete();
                Toast.makeText(c,"删除成功！!"+var2.getName(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clear() {
        this.memoryCache.clear();
        File var1 = new File(this.diskCachePath);
        if (var1.exists() && var1.isDirectory()) {
            File[] var2 = var1.listFiles();
            File[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File var6 = var3[var5];
                if (var6.exists() && var6.isFile()) {
                    var6.delete();
                }
            }
        }

    }

    private void cacheBitmapToMemory(String var1, Bitmap var2) {
        this.memoryCache.put(this.getCacheKey(var1), new SoftReference(var2));
    }

    private void cacheBitmapToDisk(final String var1, final Bitmap var2) {
        this.writeThread.execute(new Runnable() {
            public void run() {
                if (DeleteCaChe.this.diskCacheEnabled) {
                    BufferedOutputStream var1x = null;

                    try {
                        var1x = new BufferedOutputStream(new FileOutputStream(new File(DeleteCaChe.this.diskCachePath, DeleteCaChe.this.getCacheKey(var1))), 2048);
                        var2.compress(Bitmap.CompressFormat.PNG, 100, var1x);
                    } catch (FileNotFoundException var11) {
                        var11.printStackTrace();
                    } finally {
                        try {
                            if (var1x != null) {
                                var1x.flush();
                                var1x.close();
                            }
                        } catch (IOException var10) {
                            ;
                        }

                    }
                }

            }
        });
    }

    private Bitmap getBitmapFromMemory(String var1) {
        Bitmap var2 = null;
        SoftReference var3 = (SoftReference)this.memoryCache.get(this.getCacheKey(var1));
        if (var3 != null) {
            var2 = (Bitmap)var3.get();
        }

        return var2;
    }

    private Bitmap getBitmapFromDisk(String var1) {
        Bitmap var2 = null;
        if (this.diskCacheEnabled) {
            String var3 = this.getFilePath(var1);
            File var4 = new File(var3);
            if (var4.exists()) {
                var2 = BitmapFactory.decodeFile(var3);
            }
        }

        return var2;
    }

    private String getFilePath(String var1) {
        return this.diskCachePath + this.getCacheKey(var1);
    }

    private String getCacheKey(String var1) {
        if (var1 == null) {
            throw new RuntimeException("Null url passed in");
        } else {
            return var1.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
    }
}
