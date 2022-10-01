package com.example.algoview.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.example.algoview.ui.algoview.Heuristic
import androidx.lifecycle.ViewModel

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPref:SharedPreferences
    var tileSize = 40f
    var animationTime = 8L
        set(value) {
            println("value: $value")
            field=(3-value)*8L
        }
    var heuristic= Heuristic.MANHATTAN
    var diagonalMove=false
        set(value) {
            if (value)
                this.heuristic = Heuristic.EUCLIDEAN
            else
                this.heuristic = Heuristic.MANHATTAN
            field=value
        }

    init {
        sharedPref = application.getSharedPreferences("com.example.algoview.ui.SETTINGS", Context.MODE_PRIVATE)
        tileSize = sharedPref.getFloat("tileSize", 40f)
        animationTime = sharedPref.getLong("AnimationTime", 2)
        diagonalMove = sharedPref.getBoolean("diagonalMove", false)

    }
    fun save(){
        with (sharedPref.edit()) {
            putFloat("tileSize", tileSize)
            putLong("AnimationTime", (animationTime/-8+3))
            putBoolean("diagonalMove",diagonalMove)
            commit()
        }
    }
    override fun onCleared() {
        save()
        super.onCleared()
    }
}