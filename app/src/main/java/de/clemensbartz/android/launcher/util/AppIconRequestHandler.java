// package de.clemensbartz.android.launcher.util;
//
// import android.content.ComponentName;
// import android.content.Context;
// import android.content.pm.PackageManager;
// import android.graphics.Bitmap;
// import android.graphics.BitmapFactory;
// import android.graphics.Canvas;
// import android.graphics.drawable.Drawable;
// import android.util.LruCache;
//
// import com.squareup.picasso.Picasso;
// import com.squareup.picasso.Request;
// import com.squareup.picasso.RequestHandler;
//
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
//
// @Deprecated
// class AppIconRequestHandler extends RequestHandler {
//     private static final String SCHEME = "appicon";
//     private final Context context;
//
//     // 简单内存缓存
//     private static final LruCache<String, byte[]> iconCache =
//             new LruCache<>(2000); // 可以调大小
//
//     public AppIconRequestHandler(Context context) {
//         this.context = context;
//     }
//
//     @Override
//     public boolean canHandleRequest(Request data) {
//         return SCHEME.equals(data.uri.getScheme());
//     }
//
//     // @Override
//     public Result load2(Request data) throws IOException {
//         String packageName = data.uri.getHost();
//         String className = data.uri.getPath();
//         if (className != null && className.startsWith("/")) {
//             className = className.substring(1);
//         }
//
//         if (packageName != null && className != null) {
//             try {
//                 PackageManager pm = context.getPackageManager();
//                 Drawable drawable = pm.getActivityIcon(new ComponentName(packageName, className));
//                 if (drawable != null) {
//                     Bitmap bitmap = drawableToBitmap(drawable);
//                     return new Result(bitmap, Picasso.LoadedFrom.DISK);
//                 }
//             } catch (PackageManager.NameNotFoundException e) {
//                 // Icon not found, will return null and use default
//             }
//         }
//         return null;
//     }
//
//     @Override
//     public Result load(Request data) throws IOException {
//         String packageName = data.uri.getHost();
//         String className = data.uri.getPath();
//         if (className != null && className.startsWith("/")) {
//             className = className.substring(1);
//         }
//
//         if (packageName != null && className != null) {
//             String key = packageName + "/" + className;
//
//             // 1. 内存查缓存
//             byte[] cached = iconCache.get(key);
//             if (cached != null) {
//                 Bitmap bitmap = BitmapFactory.decodeByteArray(cached, 0, cached.length);
//                 return new Result(bitmap, Picasso.LoadedFrom.MEMORY);
//             }
//
//             // 2. 缓存没有则从 PackageManager 拿
//             try {
//                 PackageManager pm = context.getPackageManager();
//                 Drawable drawable = pm.getActivityIcon(new ComponentName(packageName, className));
//                 if (drawable != null) {
//                     Bitmap bitmap = drawableToBitmap(drawable);
//
//                     // 转成 webp bytes 存缓存
//                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                     bitmap.compress(Bitmap.CompressFormat.WEBP, 90, baos);
//                     // bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // quality 对 PNG 无效，可以随便填
//                     byte[] dataBytes = baos.toByteArray();
//                     iconCache.put(key, dataBytes);
//
//                     return new Result(bitmap, Picasso.LoadedFrom.DISK);
//                 }
//             } catch (PackageManager.NameNotFoundException e) {
//                 // ignore
//             }
//         }
//         return null;
//     }
//
//
//     private Bitmap drawableToBitmap(Drawable drawable) {
//         int width = drawable.getIntrinsicWidth();
//         int height = drawable.getIntrinsicHeight();
//        
//         if (width <= 0 || height <= 0) {
//             width = height = 96; // Default size
//         }
//         int maxSize = 192;
//         if (width > maxSize || height > maxSize) {
//             float scale = Math.min((float) maxSize / width, (float) maxSize / height);
//             width = Math.round(width * scale);
//             height = Math.round(height * scale); 
//         }
//
//
//         Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//         Canvas canvas = new Canvas(bitmap);
//         drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//         drawable.draw(canvas);
//         return bitmap;
//     }
//
//     public static String createUri(String packageName, String className) {
//         return SCHEME + "://" + packageName + "/" + className;
//     }
// }
