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
        for (i in piecesBox){
            i.Eat = false
        }
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
        val Piece = pieceAt(pieces.col,pieces.row)!!
        if (pieces.rank == Tobitman.PAWN && pieces.player == Player.BLACK && pieces.row == 5){
            addPiece(TobitPieces(pieces.col,5,Player.BLACK,Tobitman.TOBIT, R.drawable.b_d,false))
            piecesBox.remove(Piece)
        }else if(pieces.rank == Tobitman.PAWN && pieces.player == Player.WHITE && pieces.row == 0){
            addPiece(TobitPieces(pieces.col,0,Player.WHITE,Tobitman.TOBIT, R.drawable.w_d,false))
            piecesBox.remove(Piece)
        }
    }
    fun canMoveEat(fromCol:Int,fromRow:Int,ToCol:Int,ToRow:Int): Boolean {
        return !(abs(fromCol-ToCol) >0  && abs(fromRow-ToRow) > 0)  && isSpace(ToCol,ToRow) &&  (ToRow in 0..5) && (ToCol in -1..5) && isSpace(ToCol,ToRow)
    }
    private fun canEat(pieces: TobitPieces): Boolean {
        val col = pieces.col
        val row = pieces.row
        val isWhite = pieces.player == Player.WHITE
        for (i in arrayOf(-1,1)) {
            if ( !isSpace(col+i,row)){
                if ( (pieceAt(col+i,row)!!).player == (if (isWhite) Player.BLACK else Player.WHITE) && isSpace(col+2*i,row) && (row!=0) && (row!=5) && (col+i>-1) && (col+i<5) ){
                    return true
                }
            }
            if (!isSpace(col,row+i)){
                if( (pieces.rank == Tobitman.TOBIT || (pieces.rank== Tobitman.PAWN && i == (if (isWhite) -1 else 1))) && (pieceAt(col,row+i)!!).player == (if (isWhite) Player.BLACK else Player.WHITE)  && isSpace(col,row+2*i) && (col != 5) && (col!=-1) && (row+i>0) && (row+i<5)){
                    return true
                }
            }
        }
        return false
    }
    fun checkCanEat(col:Int,row:Int,pieces: TobitPieces){
//        pieces.Eat = canEat(pieces)
//        for (i in arrayOf(-1,1)){
//            if (!isSpace(pieces.col + i,pieces.row)){
//                pieceAt(pieces.col + i,pieces.row)!!.Eat = canEat(pieceAt(pieces.col + i,pieces.row)!!)
//            }
//            if (!isSpace(pieces.col ,pieces.row+ i)){
//                pieceAt(pieces.col,pieces.row + i)!!.Eat = canEat(pieceAt(pieces.col ,pieces.row+ i)!!)
//                Log.d(TAG,pieces.col.toString()+(pieces.row + i).toString()+ pieceAt(pieces.col,pieces.row + i)!!.Eat)
//            }
//            if (!isSpace(col + i,row)){
//                pieceAt(col + i,row)!!.Eat = canEat(pieceAt(col + i,row)!!)
//            }
//            if (!isSpace(col ,row+ i)){
//                pieceAt(col,row + i)!!.Eat = canEat(pieceAt(col ,row+ i)!!)
//            }
//        }
        for (i in piecesBox){
            i.Eat = canEat(i)
            if (i.Eat){
                Log.d(TAG,"(${i.col},${i.row}) ${i.Eat}")

            }
        }
    }

    fun movePiece(fromCol:Int,fromRow:Int, toCol:Int,toRow:Int){
        if (fromCol == toCol && fromRow==toRow) return
        val movingPiece = pieceAt(fromCol,fromRow) ?: return
        val canEat = eatAt(if(Whiteturn) Player.WHITE else Player.BLACK)
        if (canMove(fromCol,fromRow, toCol,toRow)) {
            if ((if(canEat) (movingPiece.Eat) else true)  && movingPiece.player == (if (Whiteturn) Player.WHITE else Player.BLACK)) {
                if (!canEat && abs(fromCol - toCol) == 1 || abs(fromRow - toRow) == 1) {
                    if ((abs(fromCol - toCol) == 1) || (movingPiece.rank == Tobitman.PAWN && fromRow - toRow == (if (Whiteturn) 1 else -1)) || (movingPiece.rank == Tobitman.TOBIT)) {
                        movingPiece.Eat = false
                        piecesBox.remove(movingPiece)
                        addPiece(movingPiece.copy(col = toCol, row = toRow))
                        Whiteturn = !Whiteturn
                    }
                } else if (abs(fromCol - toCol) == 2 && !isSpace((fromCol + toCol) / 2, fromRow) && (pieceAt((fromCol + toCol) / 2, fromRow)!!).player == (if (Whiteturn) Player.BLACK else Player.WHITE) ) {
                    pieceAt((fromCol + toCol) / 2, fromRow)!!.Eat =false
                    piecesBox.remove(pieceAt((fromCol + toCol) / 2, fromRow))
                    movingPiece.Eat = false
                    piecesBox.remove(movingPiece)
                    addPiece(movingPiece.copy(col = toCol, row = toRow))
                    checkCanEat(fromCol,fromCol,pieceAt(toCol, toRow)!!)
                    if (!pieceAt(toCol, toRow)!!.Eat){
                            Whiteturn = !Whiteturn
                    }
                } else if (abs(fromRow - toRow) == 2 && !isSpace(fromCol, (fromRow + toRow) / 2) && (pieceAt(fromCol, (fromRow + toRow) / 2)!!).player == (if (Whiteturn) Player.BLACK else Player.WHITE)) {
                    pieceAt(fromCol, (fromRow + toRow) / 2)!!.Eat =false
                    piecesBox.remove(pieceAt(fromCol, (fromRow + toRow) / 2))
                    movingPiece.Eat = false
                    piecesBox.remove(movingPiece)
                    addPiece(movingPiece.copy(col = toCol, row = toRow))
                    pieceAt(toCol,toRow)!!.Eat = canEat(pieceAt(toCol,toRow)!!)
                    if (!pieceAt(toCol, toRow)!!.Eat){
                        Whiteturn = !Whiteturn
                    }
                }
                if (!isSpace(toCol, toRow)) {
                    checkCanEat(fromCol,fromCol,pieceAt(toCol, toRow)!!)
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
    private fun eatAt(player: Player): Boolean {
        for( piece1 in piecesBox){
            if (piece1.Eat == true && piece1.player== player){
                return true
            }
        }
        return false
    }
}