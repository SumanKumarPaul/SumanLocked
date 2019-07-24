package paul.suman.sumanlocked

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.file_list_rec_layout.view.*
import paul.suman.sumanlocked.helper.FarFromHome
import java.io.File
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.view.*


class FileListAdapter(private val context: Context, private val mFilePath: String) :
    RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {

    private val directory = File(mFilePath)
    private val fileList = directory.listFiles()?.sorted()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.file_list_rec_layout, parent, false)
        return FileListViewHolder(view)
    }

    override fun getItemCount() = fileList?.size ?: 0

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {

//        val position = holder.adapterPosition

        if (fileList != null) {

            val file = fileList[position]

            if (file.isFile) {

                val fileUri = Uri.fromFile(file)
                val fileType = getMimeType(fileUri.toString()).toString().split("/")[0]

                holder.fileContainer.setOnClickListener {
                    Toast.makeText(context, "This is not a Video", Toast.LENGTH_SHORT).show()
                }

                when (fileType) {
                    "video" -> {

                        val thumbnailBitmap = ThumbnailUtils.createVideoThumbnail(
                            file.absolutePath,
                            MediaStore.Video.Thumbnails.MINI_KIND
                        )

                        if (thumbnailBitmap != null) {
                            holder.fileIcon.layoutParams.height = 150
                            holder.fileIcon.layoutParams.width = 150

                            Glide.with(context).load(thumbnailBitmap).into(holder.fileIcon)

//                            holder.fileIcon.setImageBitmap(thumbnailBitmap)

                        } else {
                            holder.fileIcon.setImageResource(R.drawable.ic_video)
                        }

                        holder.fileContainer.setOnClickListener {
                            val intent = Intent(context, VideoActivity::class.java)
                            intent.data = fileUri
                            context.startActivity(intent)
                        }
                    }

                    "image" -> {
                        holder.fileIcon.setImageResource(R.drawable.ic_image)
                    }

                    "audio" -> {
                        holder.fileIcon.setImageResource(R.drawable.ic_audio)
                    }

                    "application" -> {
                        holder.fileIcon.setImageResource(R.drawable.ic_document)
                    }

                    else -> {
                        holder.fileIcon.setImageResource(R.drawable.ic_cross)
                    }
                }


            } else if (file.isDirectory) {

                holder.fileIcon.setImageResource(R.drawable.ic_folder)

                holder.fileContainer.setOnClickListener {

                    FarFromHome.filePath = "$mFilePath/${file.name}"

                    (context as Activity).fileListRecVMainAc.adapter =
                        FileListAdapter(context, FarFromHome.filePath)

                    context.fileListRecVMainAc.adapter?.notifyDataSetChanged()

                    FarFromHome.howFarFromHome++

                }
            }

            holder.fileName.text = file?.name

        }
    }


    class FileListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileIcon: ImageView = itemView.fileIconImg
        val fileName: TextView = itemView.fileNameTV
        val fileContainer: LinearLayout = itemView.fileContainer
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

}