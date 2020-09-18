// AudioInterface.aidl
package com.player.audio;

// Declare any non-default types here with import statements

interface AudioInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void setFast(boolean isFast) ;

    void seek(int progress) ;

    void toPlaying() ;

    void onStop() ;

    void onRestart();

    void playing() ;


}