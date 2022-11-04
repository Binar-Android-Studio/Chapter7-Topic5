package com.example.chapter7topic5

import android.content.ContentResolver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.chapter7topic5.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private var imageMultiPart : MultipartBody.Part? = null
    private var imageUri : Uri? = Uri.EMPTY
    private var imageFile : File? = null
    private val viewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            addImage.setOnClickListener {
                SetImage()
            }

            btnRegister.setOnClickListener {
                registerButton()
            }
        }
    }

    private fun SetImage() {
        getContent.launch("image/*")
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val contentResolver: ContentResolver = this.contentResolver
            val type = contentResolver.getType(it)
            imageUri = it

            val fileNameimg = "${System.currentTimeMillis()}.png"
            binding.addImage.setImageURI(it)
            Toast.makeText(this, "$imageUri", Toast.LENGTH_SHORT).show()

            val tempFile = File.createTempFile("and1-", fileNameimg, null)
            imageFile = tempFile
            val inputstream = contentResolver.openInputStream(uri)
            tempFile.outputStream().use { result ->
                inputstream?.copyTo(result)
            }
            val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
            imageMultiPart = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
        }
    }

    private fun registerButton() {
        val fullname = binding.etFullName.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val email = binding.etEmail.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val password = binding.etPassword.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val phoneNumber = binding.etNomorTelp.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val address = binding.etAlamat.text.toString().toRequestBody("multipart/form-data".toMediaType())
        val city = binding.etKota.text.toString().toRequestBody("multipart/form-data".toMediaType())

        viewModel.register(fullname,email,password,phoneNumber,address,imageMultiPart!!,city)
        viewModel.registerObserver().observe(this){
            if (it != null){
                Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
            }
        }

    }
}