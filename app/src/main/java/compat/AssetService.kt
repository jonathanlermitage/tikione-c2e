package compat

import android.content.res.AssetManager

interface AssetService {

    fun getAssetManager() : AssetManager?

    /** Android app should set AssetManager from main view. */
    fun setAssetManager(asset: AssetManager)
}
