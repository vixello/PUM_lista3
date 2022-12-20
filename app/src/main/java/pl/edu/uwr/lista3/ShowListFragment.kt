package pl.edu.uwr.lista3

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import pl.edu.uwr.lista3.databinding.DialogUpdateBinding
import pl.edu.uwr.lista3.databinding.FragmentShowlistBinding

class ShowListFragment : Fragment(){
    private lateinit var binding: FragmentShowlistBinding

    private lateinit var list: ListForClass
    private final lateinit var listIndex: String
    private val dbHandler by lazy { DBhelper(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listIndex = arguments?.getString("moduleID").toString()

      //  arguments?.let { taskId = it.getString("taskId").toString() }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentShowlistBinding.inflate(inflater, container, false)
        list = dbHandler.getLists().find{ list -> list.id.toString() == listIndex }!!
        binding.textViewId.text = list.name
        binding.textViewDescription.text = list.description
        binding.textViewTasks.text = list.tasks

        binding.imageViewEdit.setOnClickListener{
            showList(list)
        }
        binding.imageViewDelete.setOnClickListener{
            dbHandler.deleteList(list)
            val action = ShowListFragmentDirections.toListsViewFragment()
            Navigation.findNavController(binding.root).navigate(action)

        }
        binding.backbutton.setOnClickListener{
            val action = ShowListFragmentDirections.toListsViewFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun showList(item: ListForClass) {

        val dialog = Dialog(requireContext())
        val dialogBinding = DialogUpdateBinding.inflate(LayoutInflater.from(context))

        dialog.apply {
            setCancelable(false)
            setContentView(dialogBinding.root)
        }
        dialogBinding.apply {
            editTextNameUpdate.setText(item.name)
            editTextDescriptionUpdate.setText(item.description)
            editTextTasksUpdate.setText(item.tasks)
            buttonUpdate.setOnClickListener {
                updateFragment(dialogBinding, item, dialog)


            }
            buttonCancel.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }
    private fun updateFragment(dialogBinding: DialogUpdateBinding, item: ListForClass, dialog: Dialog)
    {
        val updateName = dialogBinding.editTextNameUpdate.text.toString()
        val updateIndex = dialogBinding.editTextDescriptionUpdate.text.toString()
        val updateTasks = dialogBinding.editTextTasksUpdate.text.toString()
        if (updateName.isNotEmpty() && updateIndex.isNotEmpty()) {
            dbHandler.updateList(item.id, updateName, updateIndex,updateTasks)
            list = dbHandler.getLists().find { list-> list.id.toString() == listIndex }!!
            binding.textViewId.text = list.name
            binding.textViewDescription.text = list.description
            binding.textViewTasks.text = list.tasks
            dialog.dismiss()
        }
    }
    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }
}