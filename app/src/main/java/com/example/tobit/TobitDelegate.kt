package com.example.tobit

interface TobitDelegate {
    fun PieceAt(col:Int,row:Int): TobitPieces?
    fun movePiece(fromCol:Int,fromRow:Int, toCol:Int,toRow:Int)
}