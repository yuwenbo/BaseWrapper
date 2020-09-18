// IAudioInterface.aidl
package usage.ywb.wrapper.audio;

// Declare any non-default types here with import statements
import usage.ywb.wrapper.audio.entity.AudioEntity;


interface IAudioInterface {

    void setResource(in AudioEntity entity);

    void prepare();

    void play();

    boolean isPlaying();

    void seek(int progress) ;

    void stop();

    void release();

}