// IAudioController.aidl
package usage.ywb.wrapper.audio;

// Declare any non-default types here with import statements
import usage.ywb.wrapper.audio.entity.AudioEntity;
import android.graphics.Bitmap;

interface IAudioController {

    void setResource(in AudioEntity entity);

    Bitmap getBitmap();

    void prepare();

    void play();

    boolean isPlaying();

    void seek(int progress) ;

    void stop();

    void release();

}