package pl.edu.uwr.lista3

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import pl.edu.uwr.lista3.databinding.DialogUpdateBinding
import pl.edu.uwr.lista3.databinding.FragmentShowlistBinding
import pl.edu.uwr.lista3.databinding.FragmentListsviewBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class ShowListFragment : Fragment(){
    private lateinit var binding: FragmentShowlistBinding
    private val fraglist by lazy { ListsViewFragment() }

    private lateinit var list: ListForClass
    private final lateinit var listIndex: String
    private val dbHandler by lazy { DBhelper(requireContext()) }
    private lateinit var pictureAbsolutePath: Uri

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
        binding.imageViewTasks.setImageURI(Uri.parse(list.image))


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
            editImageTasksUpdate.setOnClickListener{
                openCamera()
            }
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
        val updateImage = pictureAbsolutePath.toString()
        if (updateName.isNotEmpty() && updateIndex.isNotEmpty()) {
            dbHandler.updateList(item.id, updateName, updateIndex,updateTasks,updateImage)
            list = dbHandler.getLists().find { list-> list.id.toString() == listIndex }!!
            binding.textViewId.text = list.name
            binding.textViewDescription.text = list.description
            binding.textViewTasks.text = list.tasks
            binding.imageViewTasks.setImageURI(Uri.parse(list.image))
            dialog.dismiss()
        }
    }
    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            launchCamera()
        }
    }
    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageViewTasks.setImageBitmap(imageBitmap)
            pictureAbsolutePath = saveImage(imageBitmap) // zapis pliku oraz ścieżki
        }
    }
    private fun openCamera(){
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> {
                launchCamera() // włączam aplikację przez implicit intent
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA) -> {
                fraglist.showMessageOKCancel(getString(R.string.rationale_camera)) // Rationale
            }
            else -> {
                requestCameraPermissionLauncher
                    .launch(Manifest.permission.CAMERA) // jeżeli nie to nic nie robię
            }
        }
    }
    private fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCamera.launch(intent)
    }

    private fun saveImage(bitmap: Bitmap): Uri {
        var file = requireContext().getDir("myGalleryKotlin", Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
}