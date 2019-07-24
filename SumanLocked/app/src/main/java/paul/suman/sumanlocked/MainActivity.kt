package paul.suman.sumanlocked

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.LinearLayoutManager
import paul.suman.sumanlocked.helper.FarFromHome.filePath
import paul.suman.sumanlocked.helper.FarFromHome.howFarFromHome
import android.app.ActivityManager
import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import java.io.File
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log.d


class MainActivity : AppCompatActivity() {


    private var shouldPause = true

    private val pass = "simbavoxboy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val nameOfLauncherPkg = defaultLauncher!!.activityInfo.packageName

        if(nameOfLauncherPkg != "paul.suman.sumanlocked") {
            val settingIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingIntent.data = Uri.parse("package:$nameOfLauncherPkg")
            startActivity(settingIntent)
        }


        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if(openLockBg.isVisible) {
            openLockBg.setOnClickListener {
                lockBg.visibility = View.GONE
                openLockBg.visibility = View.GONE
                fileListRecVMainAc.visibility = View.VISIBLE
                supportActionBar?.show()
            }
        }

        val videoPath = "/mnt/extSdCard/EdVideos"

        val directory = File(videoPath)
        val files = directory.listFiles()
        if (files == null) {
            fileFetchErrorTV.visibility = View.VISIBLE
            fileFetchErrorTV.text = "Error\n\nNo Folder called EdVideos\n\nContact Developer Suman"
            mainAcContainer.setBackgroundColor(Color.parseColor("#700000"))
        } else {
            fileFetchErrorTV.visibility = View.GONE
            fileListRecVMainAc.layoutManager = LinearLayoutManager(this)
            fileListRecVMainAc.adapter = FileListAdapter(this, videoPath)
        }

    }

    override fun onBackPressed() {

        if(lockBg.isVisible and openLockBg.isVisible) {
            lockBg.visibility = View.GONE
            openLockBg.visibility = View.GONE
            fileListRecVMainAc.visibility = View.VISIBLE
            supportActionBar?.show()
        } else {

            if (howFarFromHome == 0) {

//            finish()

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle("PASSWORD")
                alertDialog.setMessage("Enter Password")

                val input = EditText(this@MainActivity)
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                input.layoutParams = lp
                alertDialog.setView(input)

                alertDialog.setPositiveButton(
                    "SUMAN"
                ) { _, _ ->
                    val password = input.text.toString()
                    if (password == pass) {

                        shouldPause = false
                        finish()

                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Wrong Password!", Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                alertDialog.setNegativeButton(
                    "NOT Suman"
                ) { dialog, _ -> dialog.cancel() }

                alertDialog.show()

            } else {
                filePath = filePath.slice(IntRange(0, (filePath.lastIndexOf("/") - 1)))
                howFarFromHome--
                fileListRecVMainAc.adapter = FileListAdapter(this, filePath)
                fileListRecVMainAc.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (shouldPause) {
            val activityManager = applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.moveTaskToFront(taskId, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.lockMenu -> {
                lockBg.visibility = View.VISIBLE
                openLockBg.visibility = View.VISIBLE
                fileFetchErrorTV.visibility = View.GONE
                fileListRecVMainAc.visibility = View.GONE
                supportActionBar?.hide()

                openLockBg.setOnClickListener {
                    lockBg.visibility = View.GONE
                    openLockBg.visibility = View.GONE
                    fileListRecVMainAc.visibility = View.VISIBLE
                    supportActionBar?.show()
                }

            }
        }

        return super.onOptionsItemSelected(item)
    }

}
