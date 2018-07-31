package com.killer.jack.barscanerrealtime.helpers

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.view.Surface
import com.killer.jack.barscanerrealtime.utill.LogUtil

class CameraHelper(private val mCameraManager: CameraManager, private val mCameraID: String) {
    private var mCameraDevice: CameraDevice? = null
    private var mCameraSession: CameraCaptureSession? = null
    private lateinit var mPreviewSurface: Surface

    fun isOpen(): Boolean = mCameraDevice != null


    @SuppressLint("MissingPermission")
    fun openCamera(previewSurface: Surface) {
        mPreviewSurface = previewSurface
        try {
            mCameraManager.openCamera(mCameraID, mCameraCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        mCameraSession?.stopRepeating()
        mCameraSession?.close()
        mCameraSession = null

        mCameraDevice?.close()
    }

    fun getCamerasIdList(): Array<String> = mCameraManager.cameraIdList

    fun info() {
        val cameraList: Array<String> = mCameraManager.cameraIdList
        for (tmp in cameraList) {
            LogUtil.info(this, "@@@ $tmp")
            val cameraCharacter = mCameraManager.getCameraCharacteristics(tmp)
            val configurationMap = cameraCharacter.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val arrFormat = configurationMap.getOutputSizes(ImageFormat.JPEG)
            for (tmpFormat in arrFormat) {
                LogUtil.info(" w: ${tmpFormat.width}, h: ${tmpFormat.height}")
            }
        }
    }

    private fun showPreview() {
        try {
            val builder: CaptureRequest.Builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            builder.addTarget(mPreviewSurface)
            mCameraDevice!!.createCaptureSession(mutableListOf(mPreviewSurface), stateCallback(builder), null)
        } catch (cae: CameraAccessException) {
            cae.printStackTrace()
        }
    }


    private fun stateCallback(builder: CaptureRequest.Builder): CameraCaptureSession.StateCallback {
        return object : CameraCaptureSession.StateCallback() {
            override fun onActive(session: CameraCaptureSession?) {
                super.onActive(session)
                mCameraSession = session
            }

            override fun onConfigured(session: CameraCaptureSession?) {
                if (session != null) {
                    session.setRepeatingRequest(builder.build(), null, null)
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession?) {
            }


        }
    }

    val mCameraCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            mCameraDevice = camera
            showPreview()
            LogUtil.info(this, "Open camera  with id: ${mCameraDevice!!.getId()}")
        }

        override fun onDisconnected(camera: CameraDevice?) {
            mCameraDevice?.close()
            LogUtil.info("CLOSE camera  with id: ${mCameraDevice!!.getId()}")
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            mCameraDevice = null
        }
    }
}
