package com.hangyjx.syygzapp.zxing.core;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.hangyjx.syygzapp.zxing.ui.CaptureActivity;

/**
 * This thread does all the heavy lifting of decoding the images.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

	public static final String BARCODE_BITMAP = "barcode_bitmap";

	private final CaptureActivity activity;
	private final Map<DecodeHintType, Object> hints;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(CaptureActivity activity,
				 Collection<BarcodeFormat> decodeFormats, String characterSet,
				 ResultPointCallback resultPointCallback) {

		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);

		hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);

		// The prefs can't change while the thread is running, so pick them up
		// once here.
		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
			if (true) {
				decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			}
			if (true) {
				decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			}
			if (true) {
				decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
			}
		}
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		if (characterSet != null) {
			hints.put(DecodeHintType.CHARACTER_SET, characterSet);
		}
		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
				resultPointCallback);
	}

	Handler getHandler() {
		try {
			//获取handler时是阻塞的，直到run方法执行完
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity, hints);
		//这里阻塞
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
