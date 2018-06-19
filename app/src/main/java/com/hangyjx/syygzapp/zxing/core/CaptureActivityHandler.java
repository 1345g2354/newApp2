package com.hangyjx.syygzapp.zxing.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.util.Collection;

import com.hangyjx.syygzapp.R;
import com.hangyjx.syygzapp.zxing.camera.CameraManager;
import com.hangyjx.syygzapp.zxing.ui.CaptureActivity;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final CaptureActivity activity;
	private final DecodeThread decodeThread;
	private State state;
	private final CameraManager cameraManager;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureActivity activity,
								  Collection<BarcodeFormat> decodeFormats, String characterSet,
								  CameraManager cameraManager) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
				new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.restart_preview:
			restartPreviewAndDecode();
			break;
		case R.id.decode_succeeded:
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = bundle == null ? null : (Bitmap) bundle
					.getParcelable(DecodeThread.BARCODE_BITMAP);
			activity.handleDecode((Result) message.obj, barcode);
			break;
		case R.id.decode_failed:
			// We're decoding as fast as possible, so when one decode fails,
			// start another.
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
//			activity.handleDecode((Result) message.obj, null);
			break;
		case R.id.return_scan_result:
			activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
			activity.finish();
			break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			// continue
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					R.id.decode);
			activity.drawViewfinder();
		}
	}

}
