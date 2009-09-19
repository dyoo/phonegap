package plt.playlist;


import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;

import java.io.IOException;
import java.util.List;

import android.util.Log;





// Given a playlist, plays music.

// Needs a handler to queue its stuff.  MediaPlayer is supposed to be
// finicky about running in the UI thread, so all of our operations
// must run from the handler.

//
// stopped           paused
//    |
//    V
// about to play ----> playing a song ----> delaying between songs
//                            ^                   |
//                            |                   |
//                            +-------------------+
// 
//
// with every state having an arrow to "STOPPED" or PAUSED.


public class PlaylistPlayer {
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Activity activity;

    private int currentSongIndex;
    private int delayBetweenSongs;
    private List<Uri> songs;


    private enum State { ABOUT_TO_PLAY, PLAYING_A_SONG, DELAYING_BETWEEN_SONGS, STOPPED, PAUSED };

    private State currentState;


    public PlaylistPlayer(final Activity activity,
			  Handler handler,
			  final PlaylistRecord record) {

	Log.d("PlaylistPlayer", "Constructing player");

	final PlaylistPlayer that = this;
	this.activity = activity;
	this.handler = handler;
	this.handler.post(new Runnable() { public void run() { 
	    that.songs = record.getSongUris(activity);
	    that.currentSongIndex = 0;
	    that.currentState = State.STOPPED;
	    that.delayBetweenSongs = 2000;
	}});

    }

    // The following methods will queue up a sequence of songs to play.

    public void play() {
	final PlaylistPlayer that = this;

	this.handler.post(new Runnable() {
		public void run() {
		    try {
			switch(that.currentState) {
			case STOPPED:
			case DELAYING_BETWEEN_SONGS:
			    if (that.mediaPlayer == null) {
				that.mediaPlayer = new MediaPlayer();
				that.mediaPlayer.setLooping(false);
				that.mediaPlayer.setOnPreparedListener
				    (new OnPreparedListener() {
					    public void onPrepared(final MediaPlayer mp) {
						that.handler.post(new Runnable() {
							public void run() {
							    switch(that.currentState) {
							    case ABOUT_TO_PLAY:
								that.mediaPlayer.start();
								that.currentState = State.PLAYING_A_SONG;
								break;
							    case STOPPED:
								break;
							    case PAUSED:
								break;
							    case PLAYING_A_SONG:
								break;
							    case DELAYING_BETWEEN_SONGS:
								break;
							    }
							}
						    });
					    }
					});
			    
				that.mediaPlayer.setOnCompletionListener
				    (new OnCompletionListener() {
					    public void onCompletion(final MediaPlayer mp) {
						switch(that.currentState) {
						case ABOUT_TO_PLAY:
						    // shouldn't happen: reset to STOPPED.
						    that.currentState = State.STOPPED;
						    break; 
						case STOPPED:
						    // shouldn't happen: reset to STOPPED.
						    break;
						case PAUSED:
						    // shouldn't happen
						    break;
						case PLAYING_A_SONG:
						    that.mediaPlayer.release();
						    that.mediaPlayer = null;
						    that.currentSongIndex = 
							(that.currentSongIndex + 1) %
							that.songs.size();
						    that.handler.postAtTime(new Runnable() {
							    public void run() {
								switch(that.currentState) {
								case ABOUT_TO_PLAY:
								    break;
								case STOPPED:
								    break;
								case PAUSED:
								    break;
								case PLAYING_A_SONG:
								    break;
								case DELAYING_BETWEEN_SONGS:
								    that.play();
								    break;
								}
							    }
							}, SystemClock.uptimeMillis() +
							that.delayBetweenSongs);
						    that.currentState = State.DELAYING_BETWEEN_SONGS;
						    break;
						case DELAYING_BETWEEN_SONGS:
						    break;// shouldn't happen
						}

					    }
					
					});

				that.mediaPlayer.setDataSource
				    (that.activity,
				     that.songs.get(that.currentSongIndex));
				that.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			    }

			    that.currentState = State.ABOUT_TO_PLAY;
			    that.mediaPlayer.prepareAsync();
			    break;
			case PAUSED:
			    that.mediaPlayer.start();
			    that.currentState = State.PLAYING_A_SONG;
			case ABOUT_TO_PLAY:
			    break;
			case PLAYING_A_SONG:
			    break;
			};
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    });
    }
    


    public void pause() {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		    switch(that.currentState) {
		    case ABOUT_TO_PLAY:
			that.currentState = State.PAUSED;
			break;
		    case STOPPED:
			break;
		    case PAUSED:
			break;
		    case PLAYING_A_SONG:
			that.mediaPlayer.pause();
			that.currentState = State.PAUSED;
			break;
		    case DELAYING_BETWEEN_SONGS:
			that.currentState = State.PAUSED;
			break;
		    }
		}
	    });
    }


    public void stop() {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		    switch(that.currentState) {
		    case ABOUT_TO_PLAY:
			if (that.mediaPlayer != null) {
			    that.mediaPlayer.release();
			}
			that.mediaPlayer = null;
			that.currentState = State.STOPPED;
			break;
		    case STOPPED:
			break;
		    case PAUSED:
			if (that.mediaPlayer != null) {
			    that.mediaPlayer.release();
			}
			that.mediaPlayer = null;
			that.currentState = State.STOPPED;
			break;
		    case PLAYING_A_SONG:
			if (that.mediaPlayer != null) {
			    that.mediaPlayer.stop();
			    that.mediaPlayer.release();
			}
			that.mediaPlayer = null;
			that.currentState = State.STOPPED;
			break;
		    case DELAYING_BETWEEN_SONGS:
			if (that.mediaPlayer != null) {
			    that.mediaPlayer.release();
			}
			that.mediaPlayer = null;
			that.currentState = State.STOPPED;
			break;
		    }
		}
	    });
    }





}