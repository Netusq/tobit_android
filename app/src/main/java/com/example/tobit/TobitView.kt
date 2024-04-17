package com.example.tobit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


class TobitView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    // КИСТИ ДЛЯ ТЕКСТА
    val paint = Paint()
    val paint1 = Paint()
    val text_paint = Paint()
    val strok = Paint()

    // ВАЖНЫЕ НАСТРОЙКИ РАСПОЛОЖЕНИЯ ПОЛЯ
    private final val radius = 20F.dp
    private final val OriginX = (radius/2).dp
    private final val OriginY = (250F+(radius/2)).dp
    private final val CellSide = 55F.dp
    private final val textx1 = OriginX - 5f.dp
    private final val textx2 = OriginX - 10f.dp
    private final val texty = OriginY + 10f.dp
    private final val imgResIDs = setOf(
        R.drawable.w,
        R.drawable.w_d,
        R.drawable.b,
        R.drawable.b_d
    )
    private final val bitmaps = mutableMapOf<Int,Bitmap>()
    private var movingPieceBitmap: Bitmap? = null
    private var movingPiece: TobitPieces? = null
    private var fromCol:Int = -1
    private var fromRow:Int = -1
    private var movingPieceX = -1f
    private var movingPieceY = -1f
    var tobitDelegate:  TobitDelegate? = null
    init {
        loadBitmaps()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }
    override fun onDraw(canvas: Canvas) {
        drawTobitBoard(canvas)
        drawPieces(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                fromCol = -1+((event.x - 10F.dp) / 57F.dp).toInt()
                fromRow = -4+((event.y - 10F.dp) / 57F.dp).toInt()
                Log.d("touch","$fromCol,$fromRow")
                tobitDelegate?.PieceAt(fromCol,fromRow)?.let {
                    movingPiece = it
                    movingPieceBitmap = bitmaps[it.resID]
                }
            }
            MotionEvent.ACTION_MOVE -> {
                movingPieceX = event.x
                movingPieceY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val col = -1+((event.x - 10F.dp) / 57F.dp).toInt()
                val row = -4+((event.y - 10F.dp) / 57F.dp).toInt()
                tobitDelegate?.movePiece(fromCol,fromRow,col,row)
                movingPieceBitmap = null
                movingPiece = null
                fromCol=-1
                fromRow=-1
            }
        }
        return true
    }

    private fun drawPieces(canvas: Canvas){
        for (col in -1..6){
            for(row in 0..5){

                tobitDelegate?.PieceAt(col, row)
                    ?.let {
                        if (it!=movingPiece){
                            drawPiecesAt(canvas, col, row, it.resID)
                        }
                    }
            }
        }
        movingPieceBitmap?.let {
            canvas.drawBitmap(it, null, RectF(movingPieceX-CellSide/2,movingPieceY-CellSide/2,movingPieceX+CellSide/2,movingPieceY+CellSide/2),paint)
        }

    }

    private fun drawPiecesAt(canvas: Canvas,col:Int,row:Int,resID: Int){
        val bitmap = bitmaps[resID]!!
        canvas.drawBitmap(bitmap, null, RectF(OriginX+col*CellSide+36.5F.dp,OriginY+row*CellSide-18.5F.dp,OriginX+(col+1)*CellSide+18.5F.dp,OriginY+(row+1)*CellSide-36.5F.dp),paint)
    }

    private fun loadBitmaps(){
        imgResIDs.forEach(){
            bitmaps[it] = BitmapFactory.decodeResource(resources, it)
        }
    }

    // АДАПТАЦИЯ ПОД РАЗНЫЕ ЭКРАНЫ
    val Float.dp: Float
        get() = this * resources.displayMetrics.density

    private fun drawTobitBoard(canvas: Canvas){
        //ОТРИСОВКА ПОЛЯ
        for (j in 0..5) {
            for (i in 0..6) {
                drawSquereAt(canvas,j,i)

            }
        }
    }
    private fun drawSquereAt(canvas:Canvas, col:Int,row:Int){
        // КИСТЬ ДЛЯ КРУЖКОВ И ЛИНИЙ
        paint.color = Color.rgb(127,118,121)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 15F
        // КИСТЬ ДЛЯ ОБВОДКИ
        strok.color = Color.BLACK
        strok.style = Paint.Style.STROKE
        strok.strokeWidth = .7F
        // КИСТЬ ДЯЛ ТЕКСТА
        text_paint.color = Color.BLACK
        text_paint.textSize = 18f.dp
        text_paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        // ЭТО СТРАШНОЕ УЛСЛОВИЕ ЧТОБЫ НЕ ВЫВОДИЛИСЬ УГЛОВЫЕ
        if (((col != 0 && row != 0) || (col != 0 && row != 6) || (col != 5 && row != 0) && (col != 5 && row != 6)) && ((col != 0 && row != 0) && (col != 0 && row != 6) || (col != 5 && row != 0) || (col != 5 && row != 6))) {
            if (col != 5 && row != 0 && row != 6) {
                // ПАЛОЧКИ ВЕРТИКАЛЬНЫЕ
                canvas.drawLine(OriginX + row * CellSide, OriginY + radius + col * CellSide, OriginX + row * CellSide, OriginY + radius + col * CellSide + 15F.dp, paint)
            }

            if (col != 0 && col != 5 && row != 6) {
                // ПАЛОЧКИ ГОРИЗОНТАЛЬНЫЕ
                canvas.drawLine(OriginX + radius + row * CellSide, OriginY + col * CellSide, OriginX + radius + row * CellSide + 15F.dp, OriginY + col * CellSide, paint)
            }
            // ЦИФЕРКИ В КРУЖКАХ
            when (col) {
                5-> canvas.drawText(
                    (row).toString(),
                    textx1 + row * CellSide,
                    OriginY + 17.5f + col * CellSide,
                    text_paint
                )
                4 -> {
                    if (row+6 < 10){
                        canvas.drawText(
                            (row + 6).toString(),
                            textx1 + row * CellSide,
                            texty + col * CellSide - 3F.dp,
                            text_paint
                        )
                    }else{
                        canvas.drawText(
                            (row + 6).toString(),
                            textx2 + row * CellSide,
                            texty + col * CellSide - 3F.dp,
                            text_paint
                        )
                    }
                }
                3-> canvas.drawText(
                    (row + 2 * 6 + 1).toString(),
                    textx2 + row * CellSide,
                    texty + col * CellSide - 3F.dp,
                    text_paint
                )
                2 ->canvas.drawText(
                    (row + 3 * 6 + 2).toString(),
                    textx2 + row * CellSide,
                    texty + col * CellSide - 3F.dp,
                    text_paint
                )
                1-> canvas.drawText(
                    ((row + (6 * 4) + 3)).toString(),
                    textx2 + row * CellSide,
                    texty + col * CellSide - 3F.dp,
                    text_paint
                )
                0-> canvas.drawText(
                    (row + 33).toString(),
                    textx2 + row * CellSide,
                    texty + col * CellSide - 3F.dp,
                    text_paint
                )
            }
            //КРУЖКИ
            canvas.drawCircle(OriginX + row  * CellSide, OriginY + col* CellSide, radius, paint)
            //ОБВОДКА КРУЖКОВ
            canvas.drawCircle(OriginX + row  * CellSide, OriginY + col* CellSide, radius-7.5F, strok)
            canvas.drawCircle(OriginX + row  * CellSide, OriginY + col* CellSide, radius+7.5F, strok)

        }
    }
}
