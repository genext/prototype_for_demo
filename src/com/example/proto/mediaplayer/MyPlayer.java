/** 
 * <PRE>
 * Comment : <br>
 * @author : drugghanzi@funnc.com
 * @version 
 * @see 
 * </PRE>
 */
package com.example.proto.mediaplayer;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class MyPlayer extends SurfaceView implements MediaPlayerControl {
	
	public interface OnPlayerEventListener{
		public void onMediaPlayerError(int errorType);
		public void onMediaPlayerPrepared(MediaPlayer mp);
		public void onMediaPlayerError(MediaPlayer mediaPlayer, int frError, int implError);
		public void onMediaPlayerComplete(MediaPlayer mp);
		public void onMediaPlayerStarted();
	}
	
	private Uri mUri;
	private int duration;

	private SurfaceHolder surfaceHolder = null;
	private MediaPlayer player = null;
	private boolean isPrepared;
	private int videoWidth;
	private int videoHeight;
	private int surfaceWidth;
	private int surfaceHeight;
	private MediaController mediaController;

	private OnPlayerEventListener eventListener;
	private int currentBufferPercentage;

	private boolean startWhenPrepared;
	private int seekWhenPrepared;

	public MyPlayer(Context context) {
		super(context);
		initVideoView();
	}

	public MyPlayer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initVideoView();
	}

	public MyPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(videoWidth, widthMeasureSpec);
		int height = getDefaultSize(videoHeight, heightMeasureSpec);
		if (videoWidth > 0 && videoHeight > 0) {
			if (videoWidth * height > width * videoHeight) {
				height = width * videoHeight / videoWidth;
			} else if (videoWidth * height < width * videoHeight) {
				width = height * videoWidth / videoHeight;
			} else {
			}
		}
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			result = specSize;
			break;
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void initVideoView() {
		videoWidth = 0;
		videoHeight = 0;
		getHolder().addCallback(surfaceHolderCallback);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		startWhenPrepared = false;
		seekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	public void stopPlayback() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
	}
	
	public void setDataSource(AssetFileDescriptor fd){
		if (player != null) {
			try {
				player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
				player.prepare();
				player.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void openVideo() {
		if (mUri == null || surfaceHolder == null) {
			return;
		}
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		getContext().sendBroadcast(i);

		if (player != null) {
			player.reset();
			player.release();
			player = null;
		}
		try {
			player = new MediaPlayer();
			player.setOnPreparedListener(defaultMediaPlayerPreparedListener);
			player.setOnVideoSizeChangedListener(defaultMediaPlayerSizeChangedListener);
			isPrepared = false;			
			duration = -1;		
			
			player.setOnCompletionListener(defaultMediaPlayerCompletionListener);
			player.setOnErrorListener(defaultMediaPlayerErrorListener);
			player.setOnBufferingUpdateListener(defaultMediaPlayerBufferingUpdateListener);
			currentBufferPercentage = 0;
			player.setDataSource(getContext(), mUri);
			player.setDisplay(surfaceHolder);
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setScreenOnWhilePlaying(true);
			//player.prepareAsync();
			player.prepare();
			attachMediaController();
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return;
		}
	}

	public void setMediaController(MediaController controller) {
		if (mediaController != null) {
			mediaController.hide();
		}
		mediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (player != null && mediaController != null) {
			mediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this
					.getParent() : this;
			mediaController.setAnchorView(anchorView);
			mediaController.setEnabled(isPrepared);
		}
	}

	MediaPlayer.OnVideoSizeChangedListener defaultMediaPlayerSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			videoWidth = mp.getVideoWidth();
			videoHeight = mp.getVideoHeight();
			if (videoWidth != 0 && videoHeight != 0) {
				getHolder().setFixedSize(videoWidth, videoHeight);
			}
		}
	};

	MediaPlayer.OnPreparedListener defaultMediaPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			isPrepared = true;
			
			if (mediaController != null) {
				mediaController.setEnabled(true);
			}
			videoWidth = mp.getVideoWidth();
			videoHeight = mp.getVideoHeight();
			if (videoWidth != 0 && videoHeight != 0) {

				getHolder().setFixedSize(videoWidth, videoHeight);
				if (surfaceWidth == videoWidth && surfaceHeight == videoHeight) {
					if (seekWhenPrepared != 0) {
						player.seekTo(seekWhenPrepared);
						seekWhenPrepared = 0;
					}
					if (startWhenPrepared) {
						player.start();
						startWhenPrepared = false;
						if (mediaController != null) {
							mediaController.show();
						}
					} else if (!isPlaying()
							&& (seekWhenPrepared != 0 || getCurrentPosition() > 0)) {
						if (mediaController != null) {
							mediaController.show(0);
						}
					}
				}
			} else {
				if (seekWhenPrepared != 0) {
					player.seekTo(seekWhenPrepared);
					seekWhenPrepared = 0;
				}
				if (startWhenPrepared) {
					player.start();
					startWhenPrepared = false;
				}
			}
			if(eventListener != null){
				eventListener.onMediaPlayerPrepared(mp);
			}
			
		}
	};

	private MediaPlayer.OnCompletionListener defaultMediaPlayerCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			if (mediaController != null) {
				mediaController.hide();
			}
			
			if(eventListener != null){
				eventListener.onMediaPlayerComplete(mp);
			}			
		}
	};

	private MediaPlayer.OnErrorListener defaultMediaPlayerErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			if (mediaController != null) {
				mediaController.hide();
			}

			if (eventListener != null) {
				eventListener.onMediaPlayerError(player, framework_err,impl_err);
				return true;
			}
			
			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener defaultMediaPlayerBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			currentBufferPercentage = percent;
		}
	};	

	private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			surfaceWidth = w;
			surfaceHeight = h;
			if (player != null && isPrepared && videoWidth == w
					&& videoHeight == h) {
				if (seekWhenPrepared != 0) {
					player.seekTo(seekWhenPrepared);
					seekWhenPrepared = 0;
				}
				player.start();
				if (mediaController != null) {
					mediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			surfaceHolder = holder;
			openVideo();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			surfaceHolder = null;
			if (mediaController != null)
				mediaController.hide();
			if (player != null) {
				player.reset();
				player.release();
				player = null;
			}
		}
	};

	/*@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isPrepared && player != null && mediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}*/

	/*@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isPrepared && player != null && mediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}*/

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isPrepared && keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_ENDCALL && player != null
				&& mediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
					|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (player.isPlaying()) {
					pause();
					mediaController.show();
				} else {
					start();
					mediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
					&& player.isPlaying()) {
				pause();
				mediaController.show();
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mediaController.isShowing()) {
			mediaController.hide();
		} else {
			mediaController.show();
		}
	}*/
	
	public void prepareAsync(){
		if (player != null){
			player.prepareAsync();
		}
	}

	public void start() {
		if (player != null && isPrepared) {
			player.start();
			startWhenPrepared = false;
		} else {
			startWhenPrepared = true;
		}		
		
		if(eventListener != null){
			eventListener.onMediaPlayerStarted();
		}
	}

	public void pause() {
		if (player != null && isPrepared) {
			if (player.isPlaying()) {
				player.pause();
			}
		}
		startWhenPrepared = false;		
	}
	
	public void stop(){
		if (player != null && isPrepared) {
			if (player.isPlaying()) {
				player.stop();
			}
		}
	}

	public int getDuration() {
		if (player != null && isPrepared) {
			if (duration > 0) {
				return duration;
			}
			duration = player.getDuration();
			return duration;
		}
		duration = -1;
		return duration;
	}

	public int getCurrentPosition() {
		if (player != null && isPrepared) {
			return player.getCurrentPosition();
		}
		return 0;
	}

	public void seekTo(int msec) {
		if (player != null && isPrepared) {
			player.seekTo(msec);
		} else {
			seekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		if (player != null && isPrepared) {
			return player.isPlaying();
		}

		return false;

	}

	public int getBufferPercentage() {
		if (player != null) {
			return currentBufferPercentage;
		}
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}	
	
	public void setOnPlayerEventListener(OnPlayerEventListener l){
		this.eventListener = l;
	}
	
	public void setLooping(boolean bool){		
		if (player != null) {
			this.player.setLooping(bool);			
		}
	}
	
	public boolean isLooping(){
		return player.isLooping();
	}
	
}