package com.example.algoview.ui.algoview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.util.*


class Algorithms(gridView: GridView, animationTime: Long, heuristic: Heuristic, algorithm: Int, context: Context): Thread(){ //class that generates the graph and executes Dijkstra and A*, it has to be executed in a worker thread to update the grid view
    private var heuristic: Heuristic
    private var context: Context
    private var gridView: GridView
    private var animationTime: Long
    private lateinit var graph: Graph
    private val algorithm: Int
    private var running = true
    init {
        this.gridView=gridView
        this.animationTime=animationTime
        this.heuristic=heuristic
        this.context=context
        this.algorithm=algorithm
    }
    private fun toast(text: String){
        if (running && gridView.getRunning()) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sleep() {
        if(running && gridView.getRunning()) {
            try {
                sleep(animationTime)
            } catch (e: InterruptedException) {
                running = false
            }
        }
    }
    private fun getGraph() {
        val grid=gridView.grid
        val edges = arrayOf(Pair(-1,1), Pair(1,-1),Pair(1,1),Pair(-1,-1))
        graph= Graph(heuristic)
        val checkAdjacent = if (heuristic== Heuristic.MANHATTAN){
            arrayOf(Pair(-1,0), Pair(0,-1), Pair(1,0), Pair(0,1))
        } else{
            arrayOf(Pair(-1,0), Pair(0,-1), Pair(1,0), Pair(0,1), Pair(-1,1), Pair(1,-1),Pair(1,1),Pair(-1,-1))
        }
        var count = 0
        for(i in grid.indices) {
            for (j in grid[i].indices) {
                if (!gridView.getRunning() || !running) return
                if(grid[i][j]!=TileType.WALL.type){
                    graph.addVertex(count, i, j)
                    count += 1
                }

            }
        }
        for(i in grid.indices) {
            for (j in grid[i].indices) {
                val v1=graph.getVertex(i, j)
                if(v1 != null) {
                    for (offset in checkAdjacent) {
                        if (!gridView.getRunning() || !running) return
                        val x = offset.first + j
                        val y = offset.second + i
                        if (y in grid.indices && x in grid[i].indices) {
                            val v2= graph.getVertex(y, x)
                            if(v2 != null) {
                                if (offset in edges)
                                    graph.addEdge(v1, v2, 1.415)
                                else
                                    graph.addEdge(v1, v2, 1.0)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun dijkstra(start: Vertex, stop: Vertex) {
        val queue = PriorityQueue<Vertex>()
        queue.add(start)
        start.distance=0.0
        var ex: Vertex?=start
        while(!queue.isEmpty()){
            val extracted = queue.poll() ?: continue
            ex=extracted
            extracted.discovered = true
            if(extracted==stop)
                break
            if(gridView.getGrid(extracted.i,extracted.j)==TileType.EMPTY.type || gridView.getGrid(extracted.i,extracted.j)==TileType.FRINGE.type){
                if (!gridView.getRunning() || !running) return
                gridView.setGrid(extracted.i,extracted.j,TileType.SEARCH)
                this.sleep()
                if (!gridView.getRunning() || !running) return
            }
            for (i in extracted.edges.indices) {
                val edge = extracted.edges[i]
                val neighbor = edge.destination
                if (!neighbor.discovered && gridView.getGrid(neighbor.i,neighbor.j)==TileType.EMPTY.type){
                    if (!gridView.getRunning() || !running) return
                    gridView.setGrid(neighbor.i,neighbor.j,TileType.FRINGE)
                    this.sleep()
                    if (!gridView.getRunning() || !running) return
                }
                    if (neighbor.distance > (extracted.distance + edge.weight)){
                        neighbor.distance = extracted.distance + edge.weight
                        neighbor.parent = extracted
                        queue.remove(neighbor)
                        queue.add(neighbor)
                    }

            }
        }
        if(ex==stop){ 
            toast("Path found!")
            var current: Vertex? = ex
            while (current != null) {
                if (!gridView.getRunning() || !running) return
                gridView.setGrid(current.i,current.j,TileType.SOLUTION)
                this.sleep()
                if (!gridView.getRunning() || !running) return
                current = current.parent
            }


        }
        else
            toast("Path not found!")

    }
    private fun aStar(start: Vertex, stop: Vertex) {
        val comparator = Comparator<Vertex> { arg0, arg1 -> arg0.f.compareTo(arg1.f) }
        val queue = PriorityQueue(1, comparator)
        start.distance = 0.0
        start.f = 0.0
        queue.add(start)
        var ex = start
        while (!queue.isEmpty()) {
            val extracted = queue.poll()
            extracted!!.discovered = true
            if (gridView.getGrid(extracted.i,extracted.j)==TileType.EMPTY.type || gridView.getGrid(extracted.i,extracted.j)==TileType.FRINGE.type) {
                if (!gridView.getRunning() || !running) return
                gridView.setGrid(extracted.i, extracted.j, TileType.SEARCH)
                this.sleep()
                if (!gridView.getRunning() || !running) return
            }
            ex=extracted
            if (extracted === stop)
                break

            for (i in extracted.edges.indices) {
                val edge = extracted.edges[i]
                val neighbor = edge.destination
                if (!neighbor.discovered) {
                    if (gridView.getGrid(neighbor.i,neighbor.j)==TileType.EMPTY.type) {
                        if (!gridView.getRunning() || !running) return
                        gridView.setGrid(neighbor.i, neighbor.j, TileType.FRINGE)
                        this.sleep()
                        if (!gridView.getRunning() || !running) return
                    }
                    graph.heuristic(neighbor, stop)
                    if (neighbor.f > extracted.f + edge.weight) {
                        neighbor.distance = extracted.distance + edge.weight
                        graph.heuristic(neighbor, stop)
                        neighbor.f = neighbor.distance + neighbor.heuristic
                        neighbor.parent = extracted
                        queue.remove(neighbor)
                        queue.add(neighbor)
                    }
                }
            }
        }
        if(ex==stop) {
            toast("Path found!")
            var current: Vertex? = ex
            while (current != null) {
                if (gridView.getGrid(current.i,current.j)==TileType.SEARCH.type) {
                    if (!gridView.getRunning() || !running) return
                    gridView.setGrid(current.i, current.j, TileType.SOLUTION)
                    this.sleep()
                    if (!gridView.getRunning() || !running) return
                }
                current = current.parent
            }
        }
        else
            toast("Path not found!")
    }


    override fun run(){
        if(gridView.setRunning(true)){
            gridView.resetGrid()
            getGraph()
            val start = graph.getVertex(gridView.getStart().first, gridView.getStart().second)
            val stop = graph.getVertex(gridView.getStop().first, gridView.getStop().second)
            if (start!=null && stop!=null && running)
                when (algorithm) {
                    Algorithm.DIJKSTRA.type -> dijkstra(start, stop)
                    Algorithm.ASTAR.type -> aStar(start, stop)
                }
        }
        else if (gridView.getStart()==Pair(-1,-1) || gridView.getStop()==Pair(-1,-1)){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Set a start and stop tile first!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        gridView.setRunning(false)
    }

    override fun interrupt() {
        running=false
        gridView.setRunning(false)
        super.interrupt()
    }
}