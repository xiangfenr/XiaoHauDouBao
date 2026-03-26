package com.yundou.loans.http

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 *@ClassName: ApiService
 *@Deseription: 定义基础请求类型
 */
interface ApiService {

    @GET
    @JvmSuppressWildcards
    fun get(@Url url: String, @QueryMap maps: Map<String, Any>): Observable<ResultBean<Any>>

    @GET
    @JvmSuppressWildcards
    fun get(@Url url: String): Observable<ResultBean<Any>>

    @GET
    @JvmSuppressWildcards
    fun getAny(@Url url: String, @QueryMap maps: Map<String, Any>): Observable<Any>

    @GET
    @JvmSuppressWildcards
    fun getAny(@Url url: String): Observable<Any>

    @FormUrlEncoded
    @POST
    @JvmSuppressWildcards
    fun post(@Url url: String, @FieldMap maps: Map<String, Any>): Observable<ResultBean<Any>>

    @POST
    @JvmSuppressWildcards
    fun post(@Url url: String): Observable<ResultBean<Any>>

    @FormUrlEncoded
    @POST
    @JvmSuppressWildcards
    fun postAny(@Url url: String, @FieldMap maps: Map<String, Any>): Observable<Any>

    @POST
    @JvmSuppressWildcards
    fun postAny(@Url url: String): Observable<Any>

    @Multipart
    @POST
    @JvmSuppressWildcards
    fun uploadFile(
        @Url url: String,
        @FieldMap maps: Map<String, Any>,
        @Part file: MultipartBody.Part
    ): Observable<ResultBean<Any>>

    @Multipart
    @POST
    @JvmSuppressWildcards
    fun uploadFileAny(
        @Url url: String,
        @FieldMap maps: Map<String, Any>,
        @Part file: MultipartBody.Part
    ): Observable<Any>

    @Multipart
    @POST
    @JvmSuppressWildcards
    fun uploadFiles(
        @Url url: String,
        @FieldMap maps: Map<String, Any>,
        @Part file: List<MultipartBody.Part>
    ): Observable<ResultBean<Any>>

    @Multipart
    @POST
    @JvmSuppressWildcards
    fun uploadFilesAny(
        @Url url: String,
        @FieldMap maps: Map<String, Any>,
        @Part file: List<MultipartBody.Part>
    ): Observable<Any>

    @Streaming
    @GET
    fun downloadFile(
        @Header("RANGE") start: String?,
        @Url url: String?
    ): Observable<ResponseBody>

    @Streaming
    @GET
    suspend fun download(
        @Header("RANGE") start: String? = "0",
        @Url url: String?
    ): Response<ResponseBody>

    @Streaming
    @GET
    fun downloadFile(@Url url: String?): Observable<ResponseBody>

    @Streaming
    @GET
    fun downloadFiles(@Url url: String?): Observable<ResponseBody>
}

