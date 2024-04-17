package com.example.tobit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import com.example.tobit.TobitGame.reset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    //НАЖАТИЕ НА КНОПКУ ПЕРЕНОСИТ НА ДРУГУЮ АКТИВИТИ(ИГРА)
    fun startGameWithFriend(view: View) {
        val WithFriend =Intent(
            this,
            Game::class.java
        )

        WithFriend.putExtra("mode",0)
        startActivity(WithFriend)
        reset()
    }
    fun startGameWithPc(view: View) {
        val WithPc = Intent(
            this,
            Game::class.java
        )

        WithPc.putExtra("mode",1)
        startActivity(WithPc)
        reset()
    }

}
