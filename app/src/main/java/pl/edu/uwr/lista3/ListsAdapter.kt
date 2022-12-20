package pl.edu.uwr.lista3

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import pl.edu.uwr.lista3.databinding.DialogUpdateBinding
import pl.edu.uwr.lista3.databinding.ItemRowBinding

class ListsAdapter(private val  dbHandler: DBhelper, private val context: Context):
    RecyclerView.Adapter<ListsAdapter.ViewHolder>() {


    inner class ViewHolder(private val itemBinding: ItemRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(item: ListForClass) {
            itemBinding.textViewName.text = item.name
            itemBinding.textViewDescription.text = item.description
            itemBinding.textViewId.text = item.id.toString()

            itemBinding.imageViewDelete.setOnClickListener {
                dbHandler.deleteList(item)
                notifyItemRemoved(item.id - 1)
            }
            itemBinding.imageView.setOnClickListener {

//                itemView.findNavController().navigate(
//                ListsViewFragmentDirections.toShowListFragment())
                val action = ListsViewFragmentDirections.toShowListFragment(moduleID = item.id.toString())
                Navigation.findNavController(itemView).navigate(action)
            }

        }

        private fun setupDialog(item: ListForClass) {
            val dialog = Dialog(context)
            val dialogBinding = DialogUpdateBinding.inflate(LayoutInflater.from(context))
            dialog.apply {
                setCancelable(false)
                setContentView(dialogBinding.root)
            }
            dialogBinding.apply {
                editTextDescriptionUpdate.setText(item.description)
                editTextNameUpdate.setText(item.name)
                //editTextTasksUpdate.setText(item.tasks)
                buttonUpdate.setOnClickListener {
                    updateDialog(dialogBinding, item, dialog)


                }
                buttonCancel.setOnClickListener { dialog.dismiss() }

            }
            dialog.show()
        }

        private fun updateDialog(dialogBinding: DialogUpdateBinding, item : ListForClass, dialog : Dialog)
        {
            val updateName = dialogBinding.editTextNameUpdate.text.toString()
            val updateDescription = dialogBinding.editTextDescriptionUpdate.text.toString()
            val updateTasks = dialogBinding.editTextTasksUpdate.text.toString()
            if (updateName.isNotEmpty() && updateDescription.isNotEmpty()&& updateTasks.isNotEmpty()) {
                dbHandler.updateList(item.id, updateName, updateDescription, updateTasks)
                notifyItemChanged(adapterPosition)

                dialog.dismiss()
            }
        }
        }
        override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder{
        val itemBinding = ItemRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = dbHandler.getLists()[position]
        holder.bind(item)

    }
    override fun getItemCount() = dbHandler.getLists().size

}