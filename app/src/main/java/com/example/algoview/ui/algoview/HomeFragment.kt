package com.example.algoview.ui.algoview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.algoview.databinding.FragmentAlgoBinding
import com.example.algoview.ui.ItemViewModel
import java.util.concurrent.locks.ReentrantLock

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentAlgoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var grid: GridView
    private var algorithm: Algorithms? =null
    private val viewModel: ItemViewModel by activityViewModels()
    private var sharedLock = ReentrantLock()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAlgoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val tileType = binding.tileType
        val stop = binding.stop
        val dijkstra = binding.dijkstra
        val astar = binding.astar
        val spinner = binding.tileType
        spinner.setSelection(1)
        grid = binding.gridv
        grid.tileSize=viewModel.tileSize
        tileType.onItemSelectedListener = this
        stop.setOnClickListener {
            algorithm?.interrupt()
            if (grid.getRunning()){
                Toast.makeText(this@HomeFragment.context, "Stopped", Toast.LENGTH_SHORT).show()
            }
            grid.resetGrid(true)
        }
        dijkstra.setOnClickListener {
            grid.setRunning(false)
            sharedLock.lock()
            algorithm?.interrupt()
            algorithm = this@HomeFragment.context?.let { it1 ->
                Algorithms(grid,viewModel.animationTime, viewModel.heuristic, Algorithm.DIJKSTRA.type,
                    it1
                )
            }
            algorithm?.start()
            sharedLock.unlock()
        }
        astar.setOnClickListener {
            grid.setRunning(false)
            sharedLock.lock()
            algorithm?.interrupt()
            algorithm = this@HomeFragment.context?.let { it1 ->
                Algorithms(grid,viewModel.animationTime, viewModel.heuristic, Algorithm.ASTAR.type,
                    it1
                )
            }
            algorithm?.start()
            sharedLock.unlock()
        }
        return root
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        if(!grid.getRunning())
            when(pos){
                TileType.START.type -> Toast.makeText(this@HomeFragment.context, "Select the start tile", Toast.LENGTH_SHORT).show()
                TileType.STOP.type -> Toast.makeText(this@HomeFragment.context, "Select the stop tile", Toast.LENGTH_SHORT).show()
            }
        grid.tileType=pos
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
    override fun onDestroyView() {
        algorithm?.interrupt()
        super.onDestroyView()
        _binding = null
    }

}