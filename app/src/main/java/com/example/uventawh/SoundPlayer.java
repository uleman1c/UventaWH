package com.example.uventawh;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import static android.content.Context.AUDIO_SERVICE;

public class SoundPlayer {

    private Context context;
    public SoundPool soundPool;

    private AudioManager audioManager;

    private static final int MAX_STREAMS = 5;

    public static final int streamType = AudioManager.STREAM_MUSIC;

    public boolean loaded;

    public int soundId;

    public float volume;

    SoundPlayer(Context context, int rawId){

        this.context = context;

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

        float maxVolumeIndex  = (float) audioManager.getStreamMaxVolume(streamType);

        volume = currentVolumeIndex / maxVolumeIndex;

        if (Build.VERSION.SDK_INT >= 21 ) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        else {
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        soundId = soundPool.load(context, rawId,1);

    }

    public void play(){

        if(loaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            int streamId = soundPool.play(soundId, leftVolumn, rightVolumn, 1, 0, 1f);
        }



    }

}
