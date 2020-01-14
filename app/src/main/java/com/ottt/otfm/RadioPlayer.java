package com.ottt.otfm;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.security.InvalidParameterException;

public class RadioPlayer {
    private SimpleExoPlayer player;

    public RadioPlayer(final Context context, String url) {
        if(context == null)
            throw new IllegalArgumentException("context cannot be null!");

        player = new SimpleExoPlayer.Builder(context).build();
        setMediaSource(url);
        MetadataOutput mo = new MetadataOutput() {
            @Override
            public void onMetadata(Metadata metadata) {

            }
        };
        player.getMetadataComponent().addMetadataOutput(mo);
    }

    public void setMediaSource(String url) {
        if(url == null) {
            throw new IllegalArgumentException("URL cannot be null!");
        }
        if (url.isEmpty()) {
            throw new InvalidParameterException("Invalid URL");
        }

        DataSource.Factory df = new DefaultHttpDataSourceFactory("OTFM");
        MediaSource media = new ProgressiveMediaSource.Factory(df).createMediaSource(Uri.parse(url));
        player.prepare(media);
    }


    public boolean pausePlay() {
            player.seekToDefaultPosition();
            player.setPlayWhenReady(!player.getPlayWhenReady());
            return player.getPlayWhenReady();
    }

    public void setMetadataOutput(MetadataOutput output) {
        player.getMetadataComponent().addMetadataOutput(output);
    }
}
