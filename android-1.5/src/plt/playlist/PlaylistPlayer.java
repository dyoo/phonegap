package plt.playlist;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.app.Activity;


import java.util.List;




// Given a playlist, plays music.
// Needs a handler to queue its stuff.

public class PlaylistPlayer {
    private int volume;
    private int currentSongIndex;
    private List<Uri> songs;
    private Handler handler;
    private MediaPlayer mediaPlayer;


    public PlaylistPlayer(final Activity activity,
			  Handler handler,
			  final PlaylistRecord record) {
	final PlaylistPlayer that = this;
	this.handler = handler;
	this.handler.post(new Runnable() { public void run() { 
	    that.volume = 100;
	    that.songs = record.getSongUris(activity);
	    that.currentSongIndex = 0;
	    that.mediaPlayer = new MediaPlayer();
	}});
    }



    public void play() {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		}
	    });
    }


    public void pause() {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		}
	    });
    }


    public void stop() {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		}
	    });
    }



    public int getVolume() {
	return this.volume;
    }


    public void setVolume(final int percent) {
	final PlaylistPlayer that = this;
	this.handler.post(new Runnable() {
		public void run() {
		    that.volume = percent;
		}
	    });
    }
    


}