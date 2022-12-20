package pl.edu.uwr.lista3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.uwr.lista3.databinding.FragmentListsviewBinding

class ListsViewFragment : Fragment() {
    private lateinit var binding: FragmentListsviewBinding
    private val dbHandler by lazy { DBhelper(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListsviewBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dataBaseRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ListsAdapter(dbHandler, requireContext())
        }
//        binding.showButton.setOnClickListener {
//            binding.showButton.findNavController().navigate(
//                ListsViewFragmentDirections.toShowListFragment())
//        }
        binding.addButton.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val tasks = binding.editTextTasks.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty()){
                dbHandler.addList(ListForClass(name, description,tasks ))
                binding.editTextName.text.clear()
                binding.editTextDescription.text.clear()
                binding.editTextTasks.text.clear()
            }

            binding.dataBaseRecyclerView.adapter?.notifyItemInserted(dbHandler.getLists().size)
        }

    }
    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }

}