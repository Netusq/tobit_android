package com.example.tobit

import android.util.Log
import kotlin.math.abs

object TobitGame {
    private var Whiteturn = true
    val TAG = "TEST GAME WITH ERROR"
    var piecesBox = mutableSetOf<TobitPieces>()
    init {
        reset()
    }
    private fun clear(){
        piecesBox.removeAll(piecesBox)
        Whiteturn=true
    }
    private fun addPiece(pieces: TobitPieces){
        piecesBox.add(pieces)
    }
    private fun isSpace(Col:Int, Row:Int): Boolean {
        val matching_piece = piecesBox.find { it.col == Col && it.row == Row }
        return matching_piece==null
    }
    fun canMove(fromCol:Int,fromRow:Int,ToCol:Int,ToRow:Int): Boolean {
        return !(abs(fromCol-ToCol) >0  && abs(fromRow-ToRow) > 0)  && isSpace(ToCol,ToRow) && !(fromCol == -1 && ToCol == -1) && !(fromCol == 5 && ToCol == 5) && !(fromRow == 0 && ToRow == 0) && !(fromRow == 5 && ToRow == 5) && (ToRow in 0..5) && (ToCol in -1..5)
    }
    fun transform(pieces: TobitPieces){
        val Piece = pieceAt(pieces.col,pieces.row) ?: return
        if (pieces.rank == Tobitman.PAWN && pieces.player == Player.BLACK && pieces.row == 5){
            addPiece(TobitPieces(pieces.col,5,Player.BLACK,Tobitman.TOBIT, R.drawable.b_d,false))
            piecesBox.remove(Piece)
        }else if(pieces.rank == Tobitman.PAWN && pieces.player == Player.WHITE && pieces.row == 0){
            addPiece(TobitPieces(pieces.col,0,Player.WHITE,Tobitman.TOBIT, R.drawable.w_d,false))
            piecesBox.remove(Piece)
        }
    }
    private fun canEat(pieces: TobitPieces): Boolean {
        var col = pieces.col
        var row = pieces.row
        var isWhite = pieces.player == Player.WHITE
        for (i in arrayOf(-1,1)) {
            if (pieces.player == Player.WHITE) {
                if (!isSpace(col+i,row)){
                    if (((pieceAt(col+i,row)!!).player == (if (isWhite) Player.BLACK else Player.WHITE) && isSpace(col+2*i,row)) || ((pieceAt(col,row+i)!!).player == (if (isWhite) Player.BLACK else Player.WHITE) && isSpace(col,row+2*i)) ){
                        return true
                    }
                }
            }
        }
        return false
    }
    fun checkCanEat(pieces: TobitPieces){
        pieces.Eat = canEat(pieces)
        for (i in arrayOf(-1,1)){
            if (!isSpace(pieces.col + i,pieces.row)){
                pieceAt(pieces.col + i,pieces.row)!!.Eat = canEat(pieceAt(pieces.col + i,pieces.row)!!)
            }
            if (!isSpace(pieces.col ,pieces.row+ i)){
                pieceAt(pieces.col,pieces.row + i)!!.Eat = canEat(pieceAt(pieces.col ,pieces.row+ i)!!)
            }
        }
    }
    fun movePiece(fromCol:Int,fromRow:Int, toCol:Int,toRow:Int){
        if (fromCol == toCol && fromRow==toRow) return
        val movingPiece = pieceAt(fromCol,fromRow) ?: return
        if (canMove(fromCol,fromRow, toCol,toRow)) {
            if (movingPiece.player == (if (Whiteturn) Player.WHITE else Player.BLACK)) {
                if (abs(fromCol - toCol) == 1 || abs(fromRow - toRow) == 1) {
                    if ((abs(fromCol - toCol) == 1) || (movingPiece.rank == Tobitman.PAWN && fromRow - toRow == (if (Whiteturn) 1 else -1)) || (movingPiece.rank == Tobitman.TOBIT)) {
                        piecesBox.remove(movingPiece)
                        addPiece(movingPiece.copy(col = toCol, row = toRow))
                    }
                } else if (abs(fromRow - toRow) == 2 && !isSpace((fromCol + toCol) / 2, fromRow) && (pieceAt((fromCol + toCol) / 2, fromRow)!!).player == (if (Whiteturn) Player.BLACK else Player.WHITE) ) {
                    piecesBox.remove(pieceAt((fromCol + toCol) / 2, fromRow))
                    piecesBox.remove(movingPiece)
                    addPiece(movingPiece.copy(col = toCol, row = toRow))
                } else if (abs(fromRow - toRow) == 2 && !isSpace(fromCol, (fromRow + toRow) / 2) && (pieceAt(fromCol, (fromRow + toRow) / 2)!!).player == (if (Whiteturn) Player.BLACK else Player.WHITE)) {
                    piecesBox.remove(pieceAt(fromCol, (fromRow + toRow) / 2))
                    piecesBox.remove(movingPiece)
                    addPiece(movingPiece.copy(col = toCol, row = toRow))
                }
                if (!isSpace(toCol, toRow)) {
                    Whiteturn = !Whiteturn
                    transform(pieceAt(toCol, toRow)!!)
                }
            }
        }
    }
    fun reset(){
        clear()
        for (i in 0..4){
            addPiece(TobitPieces(i,0,Player.BLACK,Tobitman.PAWN, R.drawable.b,false))
            addPiece(TobitPieces(i,5,Player.WHITE,Tobitman.PAWN, R.drawable.w,false))
        }
        for (i in  -1..5){
            addPiece(TobitPieces(i,1,Player.BLACK,Tobitman.PAWN, R.drawable.b,false))
            addPiece(TobitPieces(i,4,Player.WHITE,Tobitman.PAWN, R.drawable.w,false))
        }
    }
    fun pieceAt(col:Int,row:Int): TobitPieces? {
        for( piece in piecesBox){
            if ( col == piece.col && row == piece.row){
                return piece
            }
        }
        return null
    }
}