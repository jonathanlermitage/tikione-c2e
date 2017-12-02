package compat

import android.content.res.AssetManager

class AssetServiceImpl(private var asset: AssetManager?) : AssetService {

    override fun getAssetManager(): AssetManager? {
        return asset
    }

    override fun setAssetManager(asset: AssetManager) {
        this.asset = asset
    }
}
