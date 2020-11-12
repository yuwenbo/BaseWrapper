// IAudioController.aidl
package usage.ywb.wrapper.audio;

// Declare any non-default types here with import statements
import usage.ywb.wrapper.audio.entity.AudioEntity;
import android.graphics.Bitmap;

interface IAudioController {

    void setResource(in AudioEntity entity);

    Bitmap getBitmap();

    void prepare();

    void start();

    void resume();

    boolean isPlaying();

    void seek(int progress) ;

    void pause();

    void stop();

    void release();

}