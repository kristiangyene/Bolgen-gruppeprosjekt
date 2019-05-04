package com.example.sea.ui.map

import android.content.Context
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.ui.main.MainContract

class MapInteractor(context: Context, fileName : String) : MainContract.Interactor, MapContract.Interactor, BaseInteractor(context, fileName) {
    private var sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun getFoundAddress() : Boolean {
        return sharedPreferences.getBoolean("address", false)
    }

    override fun setFoundAddress(value: Boolean) {
        sharedPreferences.edit().putBoolean("address", value).apply()
    }
}