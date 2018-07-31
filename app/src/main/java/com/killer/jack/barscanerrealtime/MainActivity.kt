package com.killer.jack.barscanerrealtime

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window

class MainActivity : AppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        var transaction = supportFragmentManager.beginTransaction()
        var fragment: CameraFragment = CameraFragment.newInstance()
        if (bundle == null)
            transaction.add(R.id.container, fragment, fragment.javaClass.canonicalName)
        else
            transaction.replace(R.id.container, fragment, fragment.javaClass.canonicalName)
        transaction.commit()
    }
}