package br.edu.ifsp.scl.sdm.photos.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.sdm.photos.databinding.TilePhotoImageBinding

class ImageAdapter(val activityContext: Context, val ImageList: MutableList<Bitmap>):
RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(tpib: TilePhotoImageBinding): RecyclerView.ViewHolder(tpib.photoIv) {
        val photoIv: ImageView = tpib.photoIv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(TilePhotoImageBinding.inflate(LayoutInflater.from(activityContext), parent, false))

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.photoIv.setImageBitmap(ImageList[position])

    override fun getItemCount(): Int = ImageList.size
}