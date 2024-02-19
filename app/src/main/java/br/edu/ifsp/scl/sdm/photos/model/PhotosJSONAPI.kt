package br.edu.ifsp.scl.sdm.photos.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_OK

class PhotosJSONAPI(context: Context) {
    companion object {
        const val PHOTOS_ENDPOINT = "https://jsonplaceholder.typicode.com/photos"

        @Volatile
        private var INSTANCE: PhotosJSONAPI? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PhotosJSONAPI(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }

    class PhotoListRequest(
        private val responseListener: Response.Listener<PhotosList>,
        errorListener: Response.ErrorListener
    ): Request<PhotosList>(Method.GET, PHOTOS_ENDPOINT, errorListener) {
        override fun parseNetworkResponse(response: NetworkResponse?): Response<PhotosList> =
            if (response?.statusCode == HTTP_OK || response?.statusCode == HTTP_NOT_MODIFIED) {
                String(response.data).run {
                    Response.success(
                        Gson().fromJson(this, PhotosList::class.java),
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            } else {
                Response.error(VolleyError())
            }

        override fun deliverResponse(response: PhotosList?) {
            responseListener.onResponse(response)
        }
    }

    class PhotoRequest(
        private val imageUrl: String,
        private val responseListener: Response.Listener<Bitmap>,
        errorListener: Response.ErrorListener
    ) : Request<Bitmap>(Method.GET, imageUrl, errorListener) {
        override fun parseNetworkResponse(response: NetworkResponse?): Response<Bitmap> =
            if (response?.statusCode == HttpURLConnection.HTTP_OK || response?.statusCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Response.success(
                    BitmapFactory.decodeByteArray(response.data, 0, response.data.size),
                    HttpHeaderParser.parseCacheHeaders(response)
                )
            } else {
                Response.error(VolleyError())
            }


        override fun deliverResponse(response: Bitmap?) = responseListener.onResponse(response)

    }
}