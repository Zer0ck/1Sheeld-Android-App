package com.integreight.onesheeld.shields.controller;

import android.app.Activity;

import com.integreight.firmatabluetooth.ShieldFrame;
import com.integreight.onesheeld.shields.fragments.CameraFragment.CameraFragmentHandler;
import com.integreight.onesheeld.utils.ControllerParent;

public class CameraShield extends ControllerParent<CameraShield> implements
		CameraFragmentHandler {
	private CameraEventHandler eventHandler;
	private static final byte CAMERA_COMMAND = (byte) 0x15;
	private static final byte CAPTURE_METHOD_ID = (byte) 0x01;
	private static final byte FLASH_METHOD_ID = (byte) 0x02;
	private static String FLASH_MODE;
	private boolean requestCamera = false;

	public CameraShield() {

	}

	public CameraShield(Activity activity, String tag) {
		super(activity, tag);
	}

	@Override
	public ControllerParent<CameraShield> setTag(String tag) {

		return super.setTag(tag);
	}

	public void setCameraEventHandler(CameraEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		CommitInstanceTotable();
	}

	@Override
	public void onNewShieldFrameReceived(ShieldFrame frame) {

		if (frame.getShieldId() == CAMERA_COMMAND) {

			// String userId = frame.getArgumentAsString(0);

			switch (frame.getFunctionId()) {
			case FLASH_METHOD_ID:
				FLASH_MODE = frame.getArgumentAsString(0);
				eventHandler.setFlashMode(FLASH_MODE);
				break;

			case CAPTURE_METHOD_ID:
				requestCamera = true;
				eventHandler.takePicture();
				break;

			default:
				break;
			}
		}

	}

	public static interface CameraEventHandler {
		void OnPictureTaken();

		void checkCameraHardware(boolean isHasCamera);

		void takePicture();

		void setFlashMode(String flash_mode);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraFragmentIntilized() {

	}

}
