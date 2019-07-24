package paul.suman.sumanlocked

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
//    var decorView: View? = null
//    var uiImmersiveOptions: Int? = null

    private var shouldPause = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val uiImmersiveOptions = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val decorView = window?.decorView
        decorView?.systemUiVisibility = uiImmersiveOptions

        if(intent.data != null) {
            playVideo(intent.data!!)
        }
    }

    private fun playVideo(uri: Uri) {

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        exoPlayerView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))
        // media source

        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)

        player.prepare(mediaSource)
        player.playWhenReady = true
    }

    private fun killPlayer() {
        if ((::player.isInitialized) and (exoPlayerView.visibility == View.VISIBLE)) {
            player.release()
        }
    }

    override fun onStop() {
        super.onStop()
        killPlayer()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        shouldPause = false
    }

    override fun onPause() {
        super.onPause()

        if (shouldPause) {
            val activityManager = applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.moveTaskToFront(taskId, 0)
        }
    }
}
