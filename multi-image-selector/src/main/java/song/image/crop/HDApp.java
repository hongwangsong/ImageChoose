package song.image.crop;

import android.app.Application;
import android.graphics.Bitmap;

public class HDApp extends Application {
    private static HDApp instance;
    private static Bitmap singleChoose;

    public static HDApp getInstance() {
        return instance;
    }


    public void onCreate() {

        super.onCreate();
        instance = this;

    }

    public static Bitmap getSingleChoose() {
        return singleChoose;
    }
    public static void setSingleChoose(Bitmap singleChoose) {
        HDApp.singleChoose = singleChoose;
    }
}
