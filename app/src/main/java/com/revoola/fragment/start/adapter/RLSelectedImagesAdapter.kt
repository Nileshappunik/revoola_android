package com.revoola.fragment.start.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R


class RLSelectedImagesAdapter(
    private val images: MutableList<Uri>,
    private val onRemoveClick: (Uri) -> Unit
) : RecyclerView.Adapter<RLSelectedImagesAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rl_layout_item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        holder.bind(imageUri,position)
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageViewSelected: ImageView = view.findViewById(R.id.imageViewSelected)
        private val imageViewRemove: ImageView = view.findViewById(R.id.imageViewRemove)

        fun bind(uri: Uri, position: Int) {
            // Load the image using Glide or another image loading library
            Glide.with(itemView.context).load(uri).into(imageViewSelected)

            // Handle the remove button click
            imageViewSelected.setOnClickListener {
                onRemoveClick(uri)
                notifyItemRemoved(position)
            }
        }
    }
}
