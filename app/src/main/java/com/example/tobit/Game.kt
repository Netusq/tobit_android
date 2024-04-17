package com.example.tobit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log

private const val TAG = "Game"
class Game : AppCompatActivity(),TobitDelegate {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<TobitView>(R.id.tobit_view).tobitDelegate = this
        Log.d(TAG, intent.getIntExtra("mode",0).toString())

    }
    override fun PieceAt(col:Int,row:Int): TobitPieces? = TobitGame.pieceAt(col,row)




    override fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        TobitGame.movePiece(fromCol, fromRow, toCol, toRow)
        findViewById<TobitView>(R.id.tobit_view).invalidate()
    }

}
