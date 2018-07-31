package com.killer.jack.barscanerrealtime


import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.killer.jack.barscanerrealtime.helpers.CameraHelper
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {

    val CAMERA_ID: String = "0"

    private var mSurfaseHolder: SurfaceHolder? = null
    private var mPreviewSurface: Surface? = null
    private var camera: CameraHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        camera = CameraHelper(activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager, CAMERA_ID)
        surface_camera.holder.addCallback(surfaceHolderCallback)
    }

    override fun onPause() {
        super.onPause()
        camera?.closeCamera()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                CameraFragment().apply {
                    arguments = Bundle().apply { }
                }
    }

    private fun requestPermission() {
        TedPermission.with(context)
                .setDeniedMessage(R.string.camera_manual_settings_perm)
                .setPermissionListener(listenerPermissions)
                .setRationaleMessage(R.string.camera_manual_select_perm)
                .setRationaleTitle(R.string.camera_title_perm)
                .setPermissions(Manifest.permission.CAMERA)
                .check()
    }

    val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(surfaceHolder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            mPreviewSurface = surfaceHolder.surface; }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            mSurfaseHolder = null
            mPreviewSurface = null
        }

        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            mSurfaseHolder = surfaceHolder
            mPreviewSurface = surfaceHolder.surface
            openCamera()
        }

    }

    private fun openCamera() {
        if (TedPermission.isDenied(context, Manifest.permission.CAMERA)) {
            requestPermission()
        } else {
            mPreviewSurface?.let { camera?.openCamera(it) }
        }
    }

    val listenerPermissions = object : PermissionListener {
        override fun onPermissionGranted() {
            openCamera()
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()

        }
    }
}


