package br.edu.ifsp.scl.sdm.photos.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.adapter.ImageAdapter
import br.edu.ifsp.scl.sdm.photos.adapter.PhotoAdapter
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.PhotosJSONAPI
import br.edu.ifsp.scl.sdm.photos.model.PhotosListItem

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val photoList: MutableList<PhotosListItem> = mutableListOf()
    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(this, photoList)
    }
    private val imageList: MutableList<Bitmap> = mutableListOf()
    private val imageAdapter: ImageAdapter by lazy {
        ImageAdapter(this, imageList)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        amb.photosSp.apply {
            adapter = photoAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val size = imageList.size
                    imageList.clear()
                    imageAdapter.notifyItemRangeRemoved(0, size)
                    retrieveImages(arrayOf(photoList[position].url, photoList[position].thumbnailUrl))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // NSA
                }
            }
        }

        amb.photoImagesRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = imageAdapter
        }

        retrievePhotos()
    }

    private fun retrievePhotos() =
        PhotosJSONAPI.PhotoListRequest({ photoList ->
            photoList.also {
                photoAdapter.addAll(it)
            }
        }, {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        }).also {
            PhotosJSONAPI.getInstance(this).addToRequestQueue(it)
        }

    private fun retrieveImages(urls: Array<String>) =
        urls.forEach { imageUrl ->
            PhotosJSONAPI.PhotoRequest(imageUrl, { response ->
               imageList.add(response)
               imageAdapter.notifyItemInserted(imageList.lastIndex)
            }, {
                Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
            }).also {
                PhotosJSONAPI.getInstance(this).addToRequestQueue(it)
            }
        }
}