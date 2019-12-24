package ru.musintimur.photoexplorer

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import ru.musintimur.photoexplorer.data.Photo
import ru.musintimur.photoexplorer.ui.photo.PhotoFragment
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(),
    OnPhotoClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_collections))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        removePhotoPane()
    }

    private fun showPhotoPane() {
        "showPhotoPane called".logD(TAG)
        photo_detail_container.visibility = View.VISIBLE
        nav_host_fragment.view?.visibility = View.GONE
    }

    private fun removePhotoPane() {
        "removePhotoPane called".logD(TAG)
        supportFragmentManager.run {
            val fragment = findFragmentById(R.id.photo_detail_container)
            if (fragment != null) {
                beginTransaction().remove(fragment).commit()
            }
        }
        photo_detail_container.visibility = View.GONE
        nav_host_fragment.view?.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun photoDetailsRequest(photo: Photo?) {
        "photoDetailsRequest called".logD(TAG)
        val newFragment = PhotoFragment.newInstance(photo)
        supportFragmentManager.beginTransaction()
            .replace(R.id.photo_detail_container, newFragment)
            .commit()
        showPhotoPane()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        "onOptionsItemSelected called".logD(TAG)
        when (item.itemId) {
            android.R.id.home -> removePhotoPane()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        "onBackPressed called".logD(TAG)
        supportFragmentManager.run {
            findFragmentById(R.id.photo_detail_container)?.let {
                "Removing fragment".logD(TAG)
                removePhotoPane()
            } ?: super.onBackPressed()
        }
    }

    override fun onPhotoClick(photo: Photo) {
        photoDetailsRequest(photo)
    }
}
