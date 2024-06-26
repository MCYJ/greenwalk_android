package com.codehanzoom.greenwalk.utils

import com.codehanzoom.greenwalk.model.PloggingResponseBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {
    // Req Type: GET
    // Endpoint: photos
    @Multipart
    @POST("photos")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("step") step: RequestBody,
        @Part("walkingDistance") walking: RequestBody,
        @Header("Authorization") accessToken: String
    ): Call<PloggingResponseBody>
}
