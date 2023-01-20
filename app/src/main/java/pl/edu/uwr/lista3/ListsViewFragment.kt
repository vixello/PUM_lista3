package pl.edu.uwr.lista3

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.uwr.lista3.databinding.FragmentListsviewBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class ListsViewFragment() : Fragment() {
    private lateinit var binding: FragmentListsviewBinding
    private val dbHandler by lazy { DBhelper(requireContext()) }
    private lateinit var pictureAbsolutePath: Uri

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
        binding.editImageTasks.setOnClickListener{
            openCamera()
        }

        binding.addButton.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val tasks = binding.editTextTasks.text.toString()
            val image = pictureAbsolutePath.toString()

            if (name.isNotEmpty() && description.isNotEmpty()){
                dbHandler.addList(ListForClass(name, description,tasks ,image))
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
            binding.editImageTasks.setImageBitmap(imageBitmap)
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
                showMessageOKCancel(getString(R.string.rationale_camera)) // Rationale
            }
            else -> {
                requestCameraPermissionLauncher
                    .launch(Manifest.permission.CAMERA) // jeżeli nie to nic nie robię
            }
        }
    }
    fun showMessageOKCancel(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                // jeżeli ok proszę o upoważnienie
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel", null) // jeżeli nie to nic nie robię
            .create()
            .show()
    }
    private fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCamera.launch(intent)
    }
    private fun checkForErrors(): Boolean{

        if (!this::pictureAbsolutePath.isInitialized)
            return true
        return false
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