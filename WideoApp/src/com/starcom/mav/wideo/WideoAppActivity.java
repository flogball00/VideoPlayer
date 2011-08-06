package com.starcom.mav.wideo;


import java.io.IOException;



import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

public class WideoAppActivity extends Activity implements Runnable,
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener,
		SurfaceHolder.Callback, MediaController.MediaPlayerControl {
	Display currentDisplay;

	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;

	MediaPlayer mediaPlayer;
    MediaController controller;
    
    private SeekBar mSeekBar;

	int videoWidth = 0;
	int videoHeight = 0;

	boolean readyToPlay = false;
	//private ProgressBar timeline=null;
	public final static String LOGTAG = "WIDEO_PLAYER";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		surfaceView = (SurfaceView) this.findViewById(R.id.SurfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnVideoSizeChangedListener(this);

		Button playButton = (Button) findViewById(R.id.playvideoplayer);
		Button pauseButton = (Button) findViewById(R.id.pausevideoplayer);
		Button stepButton = (Button) findViewById(R.id.stepframe);
		
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mSeekBar.setProgress(0);
		
		
		//Play Button Functionality
		playButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){		
					mediaPlayer.start();
					mSeekBar.setMax(mediaPlayer.getDuration());
				//Toast.makeText(WideoAppActivity.this,"play", Toast.LENGTH_LONG).show();
			}
		});
		
		//Pause Button Functionality
		pauseButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				mediaPlayer.pause();
				//Toast.makeText(WideoAppActivity.this,"pause", Toast.LENGTH_LONG).show();
			}
		});
		
		//Step Button Functionality
		stepButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				//mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+1000);
				//Toast.makeText(WideoAppActivity.this,"step " +mediaPlayer.getCurrentPosition() , Toast.LENGTH_LONG).show();
				mediaPlayer.start();
				
				try
				   {
				   // put current thread to sleep
				   //need to convert sleep wait to variable from prefs in future	
					
				   Thread.sleep( 250 );
				   mediaPlayer.pause();
				   }
				catch ( InterruptedException e )
				   {
				   System.out.println( "some other thread woke me prematurely." );
				   }
				
			}
		});

		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser){
					mediaPlayer.seekTo(progress);
					mSeekBar.setProgress(progress);
				}
			}
		});

		Thread currentThread  = new Thread(this);
		currentThread.start();

	    
		//timeline=(ProgressBar)findViewById(R.id.timeline);
		
		
		//loading video
		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ "/Test_Movie.m4v";

		try {
			mediaPlayer.setDataSource(filePath);
		} catch (IllegalArgumentException e) {
			Log.v(LOGTAG, e.getMessage());
			finish();
		} catch (IllegalStateException e) {
			Log.v(LOGTAG, e.getMessage());
			finish();
		} catch (IOException e) {
			Log.v(LOGTAG, e.getMessage());
			finish();
		}

	    controller = new MediaController(this);
	    
		currentDisplay = getWindowManager().getDefaultDisplay();
	}

@Override
public void run() {
	// TODO Auto-generated method stub
	try {
		while(mediaPlayer != null){
			int currentPosition = mediaPlayer.getCurrentPosition();
			Message msg = new Message();
			msg.what = currentPosition;
			threadHandler.sendMessage(msg);
		}
		Thread.sleep(100);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
private Handler threadHandler = new Handler(){
	public void handleMessage(Message msg){
		//super.handleMessage(msg);
		//txt.setText(Integer.toString(msg.what));
		mSeekBar.setProgress(msg.what);
	}
};

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceCreated Called");

		mediaPlayer.setDisplay(holder);

		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			Log.v(LOGTAG, e.getMessage());
			finish();
		} catch (IOException e) {
			Log.v(LOGTAG, e.getMessage());
			finish();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(LOGTAG, "surfaceChanged Called");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceDestroyed Called");
	}

	public void onCompletion(MediaPlayer mp) {
		Log.v(LOGTAG, "onCompletion Called");
		//finish();
	}

	public boolean onError(MediaPlayer mp, int whatError, int extra) {
		Log.v(LOGTAG, "onError Called");

		if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			Log.v(LOGTAG, "Media Error, Server Died " + extra);
		} else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			Log.v(LOGTAG, "Media Error, Error Unknown " + extra);
		}

		return false;
	}

	public boolean onInfo(MediaPlayer mp, int whatInfo, int extra) {
		if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
			Log.v(LOGTAG, "Media Info, Media Info Bad Interleaving " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
			Log.v(LOGTAG, "Media Info, Media Info Not Seekable " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN) {
			Log.v(LOGTAG, "Media Info, Media Info Unknown " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
			Log.v(LOGTAG, "MediaInfo, Media Info Video Track Lagging " + extra);
		}
		 else if (whatInfo == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
			 Log.v(LOGTAG,"MediaInfo, Media Info Metadata Update " + extra);
		}
		return false;
	}

	public void onPrepared(MediaPlayer mp) {
		Log.v(LOGTAG, "onPrepared Called");
		videoWidth = mp.getVideoWidth();
		videoHeight = mp.getVideoHeight();
		
		

		if (videoWidth > currentDisplay.getWidth()
				|| videoHeight > currentDisplay.getHeight()) {
			float heightRatio = (float) videoHeight
					/ (float) currentDisplay.getHeight();
			float widthRatio = (float) videoWidth
					/ (float) currentDisplay.getWidth();

			if (heightRatio > 1 || widthRatio > 1) {
				if (heightRatio > widthRatio) {
					videoHeight = (int) Math.ceil((float) videoHeight
							/ (float) heightRatio);
					videoWidth = (int) Math.ceil((float) videoWidth
							/ (float) heightRatio);
				} else {
					videoHeight = (int) Math.ceil((float) videoHeight
							/ (float) widthRatio);
					videoWidth = (int) Math.ceil((float) videoWidth
							/ (float) widthRatio);
				}
			}
		}

		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,
				videoHeight));
		//timeline.setProgress(0);
		//timeline.setMax(mediaPlayer.getDuration());
		
		//default controller
		//controller.setMediaPlayer(this);
	    //controller.setAnchorView(this.findViewById(R.id.MainView));
	    //controller.setEnabled(true);
	    //controller.show();		
	}

	public void onSeekComplete(MediaPlayer mp) {
		Log.v(LOGTAG, "onSeekComplete Called");
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(LOGTAG, "onVideoSizeChanged Called");
	}
	
	public boolean canPause() {
		 return true;
	}

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getBufferPercentage() {
        return 0;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public void start() {
        mediaPlayer.start();
    }	
    
    @Override
	public boolean onTouchEvent(MotionEvent ev) {
        if (controller.isShowing()) {
            controller.hide();
        } else {
            controller.show();
        }
        return false;
    }

	
    
}
