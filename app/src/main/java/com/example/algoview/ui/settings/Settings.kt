package com.example.algoview.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.algoview.databinding.FragmentSettingsBinding
import com.example.algoview.ui.ItemViewModel

class Settings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tileSize = binding.tileSize
        val velocity = binding.velocity
        val diagonal = binding.diagonal
        diagonal.isChecked=viewModel.diagonalMove
        diagonal.setOnCheckedChangeListener{
                _, i -> viewModel.diagonalMove=i
        }
        velocity.check(velocity.getChildAt(viewModel.animationTime.toInt()/-8+3).id)
        println("00"+(viewModel.animationTime/-8+3).toString())
        velocity.setOnCheckedChangeListener { _, i ->
            viewModel.animationTime=(velocity.indexOfChild(velocity.findViewById<RadioButton>(i))).toLong()
        }
        tileSize.progress=viewModel.tileSize.toInt()
        tileSize.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                viewModel.tileSize=seek.progress.toFloat()
                Toast.makeText(this@Settings.context,
                    "Tile size changed to " + seek.progress + "px",
                    Toast.LENGTH_SHORT).show()
            }
        })
                // write custom code for progress is changed)
       // val options =
        //    ViewModelProvider(binding.root).get(HomeViewModel::class.java)

        return root
    }

    override fun onDestroyView() {
        viewModel.save()
        super.onDestroyView()
        _binding = null
    }
}

