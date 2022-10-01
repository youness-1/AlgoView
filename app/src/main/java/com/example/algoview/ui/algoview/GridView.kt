package com.example.algoview.ui.algoview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

enum class TileType(val type: Int) {
    EMPTY(0),
    WALL(1),
    START(2),
    STOP(3),
    SEARCH(4),
    SOLUTION(5),
    FRINGE(6)
}
enum class TileType2(val type: Int) {
    EMPTY(0),
    WALL(1),
    START(2),
    STOP(3),
    SEARCH(4),
    SOLUTION(5),
    FRINGE(6)
}

class GridView(context: Context, attributeSet: AttributeSet): View(context, attributeSet){
    private val paint :Paint = Paint()
    var tileType=TileType.WALL.type
    var tileSize = 40f
        set(value){
            field=value
            this.rows = height/field.toInt()
            this.columns= width/field.toInt()
            this.grid = Array(rows) { IntArray(columns) }
            for(i in grid.indices) {
                val rowArray = IntArray(columns)
                for(j in grid[i].indices) {
                    rowArray[j] = TileType.EMPTY.type
                }
                grid[i] = rowArray
            }
            this.paddingTop = (height-(rows*tileSize))/2
            this.paddingLeft = (width-(columns*tileSize))/2
            this.state.start=Pair(-1,-1)
            this.state.stop=Pair(-1,-1)
            invalidate()
        }
    private var rows = 0
    private var columns = 0
    var paddingLeft=0f
    var paddingTop=0f
    lateinit var grid: Array<IntArray>
    private var state = object{
        var running= AtomicBoolean(false)
        var start=Pair(-1,-1)
            set(value) {
                if (value.first in grid.indices && value.second in grid[0].indices) {
                    field = value
                    grid[value.first][value.second]=TileType.START.type
                }
            }
        var stop=Pair(-1,-1)
            set(value) {
                if (value.first in grid.indices && value.second in grid[0].indices) {
                    field = value
                    grid[value.first][value.second]=TileType.STOP.type
                }
            }
    }
    fun resetGrid(resetWalls: Boolean = false){
        for(i in grid.indices) {
            for(j in grid[i].indices) {
                if (grid[i][j]!=TileType.START.type && grid[i][j]!=TileType.STOP.type) {
                    if (!resetWalls && grid[i][j]==TileType.WALL.type) continue
                    grid[i][j] = TileType.EMPTY.type
                }
            }
        }
        postInvalidate()
    }
    fun setRunning(boolean: Boolean): Boolean{
        if (boolean && (state.start==Pair(-1,-1) || state.stop==Pair(-1,-1)))
            return false
        return !state.running.getAndSet(boolean)
    }
    fun getRunning(): Boolean {
        return state.running.get()
    }
    fun getStart(): Pair<Int,Int>{
        return state.start
    }
    fun getStop(): Pair<Int,Int>{
        return state.stop
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tileSize=tileSize
        super.onSizeChanged(w, h, oldw, oldh)
    }
    override fun onDraw(canvas: Canvas?) {
        //val stroke=3f
        val coords = object{
            var left = {j: Float -> (j*tileSize+paddingLeft)}
            var top = {i: Float -> (i*tileSize+paddingTop)}
            var right = {j: Float -> (left(j)+tileSize)}
            var bottom = {i: Float -> (top(i)+tileSize)}
        }
        for(i in grid.indices) {
            for(j in grid[i].indices){
                paint.style = Paint.Style.STROKE; paint.setARGB(255,0,0,0)
                canvas?.drawRect(coords.left(j.toFloat()),coords.top(i.toFloat()),coords.right(j.toFloat()), coords.bottom(i.toFloat()), paint)
                when(grid[i][j]){
                    TileType.WALL.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(250,0,0,0)}
                    TileType.START.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(250,0,200,0)}
                    TileType.STOP.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(250,200,0,0)}
                    TileType.SEARCH.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(150,200,100,0)}
                    TileType.FRINGE.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(70,200,100,0)}
                    TileType.SOLUTION.type -> {paint.style = Paint.Style.FILL_AND_STROKE; paint.setARGB(250,200,100,0)}
                }
                if (grid[i][j]!=TileType.EMPTY.type)
                    canvas?.drawRect(coords.left(j.toFloat()),coords.top(i.toFloat()),coords.right(j.toFloat()), coords.bottom(i.toFloat()), paint)
            }
        }
        super.onDraw(canvas)
    }
    fun setGrid(i :Int, j:Int, tile: TileType){
        if(Pair(i,j)!=state.start && Pair(i,j)!=state.stop)
            grid[i][j]=tile.type
        postInvalidate()
    }
    fun getGrid(i :Int, j:Int) = grid[i][j]



    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (state.running.get()) return false //checks if an algorithm is running
        if (motionEvent.x > (width-paddingLeft) || motionEvent.x < paddingLeft || motionEvent.y > (height-paddingTop) || motionEvent.y < paddingTop) return false //click outside canvas
        val x = ((motionEvent.x-paddingLeft) / tileSize).toInt() //conversion to grid coordinates
        val y = ((motionEvent.y-paddingTop) / tileSize).toInt()
        if (y>=grid.size || x>=grid[0].size) return false //checks if indexes are outside of bounds
        if(state.stop==Pair(y,x) || state.start==Pair(y,x)) return false //can't override start or stop
        if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
            resetGrid()
            when(tileType){
                TileType.START.type -> {
                    if(state.start!=Pair(-1,-1))
                        grid[state.start.first][state.start.second]=0
                    state.start= Pair(y,x)
                    setGrid(y,x,TileType.START)
                }
                TileType.STOP.type -> {
                    if(state.stop!=Pair(-1,-1))
                        grid[state.stop.first][state.stop.second]=0
                    state.stop = Pair(y,x)
                    setGrid(y,x,TileType.STOP)
                }
                TileType.EMPTY.type -> {
                    if(state.stop!=Pair(y,x) && state.start!=Pair(y,x))
                        setGrid(y,x,TileType.EMPTY)
                }
                else -> {
                    if (state.start!=Pair(y,x) && state.stop!=Pair(y,x))
                        setGrid(y,x,TileType.WALL)
                }
            }
        }
        invalidate()
        return true
    }

}
