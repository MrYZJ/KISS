package fr.neamar.kiss;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.util.Log;

public class CameraHandler {

	private Camera camera = null;

	public void openCamera() {
		if (camera == null)
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	public void releaseCamera() {
		try {
			if (camera != null)
				camera.release();
		} catch (Exception ex) {
			Log.e("wtf", "unable to release camera " + ex );
		} finally {
			camera = null;
		}
	}

	public Boolean getTorchState() {
		try {
			openCamera();
			Parameters parms = camera.getParameters();
			return parms.getFlashMode().equals(Parameters.FLASH_MODE_TORCH);
		} catch (Exception ex) {
			Log.e("wtf", "unable to get torch states " + ex );
			releaseCamera();
			return false;
		}
	}
	
	public boolean isTorchAvailable() {
		try {
			openCamera();
			List<String> torchModes = camera.getParameters().getSupportedFlashModes();
			return torchModes.contains(Camera.Parameters.FLASH_MODE_TORCH);
		}catch (Exception ex) {
			Log.e("wtf", "unable to check if torch is available " + ex );
		}
		return false;
	}

	public void setTorchState(Boolean state) {
		try {
			openCamera();
			Parameters parms = camera.getParameters();
			if (state)
				parms.setFlashMode(Parameters.FLASH_MODE_TORCH);
			else
				parms.setFlashMode(Parameters.FLASH_MODE_OFF);

			camera.setParameters(parms);
			if (state) {//enable torch but retain camera
				camera.startPreview();
				camera.autoFocus(new AutoFocusCallback() {
	                public void onAutoFocus(boolean success, Camera camera) {
	                }
	            });
			}
			else { //disable torch and release camera
				camera.stopPreview();
				releaseCamera();
			}
		} catch (Exception ex) {
			Log.e("wtf", "unable to set torch state "+state+" " + ex );
			releaseCamera();
		}
	}
}
