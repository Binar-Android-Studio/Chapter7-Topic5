package com.example.chapter7topic5

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chapter7topic5.model.RegistResponse
import com.example.chapter7topic5.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private var liveDataUser : MutableLiveData<RegistResponse?> = MutableLiveData()

    fun registerObserver() : MutableLiveData<RegistResponse?> = liveDataUser

    fun register(fullName : RequestBody, email : RequestBody, password : RequestBody, phoneNumber : RequestBody, address : RequestBody, image : MultipartBody.Part, city : RequestBody){
        RetrofitClient.instance.registerAccount(fullName, email, password, phoneNumber, address, image, city)
            .enqueue(object : Callback<RegistResponse> {
                override fun onResponse(
                    call: Call<RegistResponse>,
                    response: Response<RegistResponse>
                ) {
                    if (response.isSuccessful){
                        liveDataUser.postValue(response.body())
                    }else{
                        liveDataUser.postValue(null)
                        Log.d("msg", response.message())
                    }
                }

                override fun onFailure(call: Call<RegistResponse>, t: Throwable) {
                    liveDataUser.postValue(null)
                }

            })
    }
}