package song.image.crop;

import android.app.Application;
import android.graphics.Bitmap;

import java.io.File;

public class HDApp extends Application {
    private static HDApp instance;

    private File singleChooseFile;
    public static HDApp getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public File getSingleChooseFile() {
        return singleChooseFile;
    }

    public void setSingleChooseFile(File singleChooseFile) {
        if(singleChooseFile ==null){//释放并删除已有图片
            if(this.singleChooseFile!=null && this.singleChooseFile.exists()){
                this.singleChooseFile.delete();
            }
        }else{
            this.singleChooseFile = singleChooseFile;
        }
    }
}
