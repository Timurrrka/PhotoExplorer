package ru.musintimur.photoexplorer

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()
    ,OnSearchClick
{
    private lateinit var navController: NavController
    private var searchFunction: (() -> Unit)? = null
    private var searchHint: String = ""
    private lateinit var searchView: SearchView
    private lateinit var searchIcon: MenuItem
    private lateinit var searchBar: MenuItem
    private val prefs: SharedPreferences by lazy { getSharedPreferences(Preferences.PREFERENCES.fileName, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_collections
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        searchIcon = menu.findItem(R.id.menu_item_search)
        searchBar = menu.findItem(R.id.app_bar_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchBar.actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView.run {
            setSearchableInfo(searchableInfo)
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    prefs.edit().putString(Properties.PREF_SEARCH_QUERY.alias, query).apply()
                    onSearchClick()
                    hideSearchBar()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            setOnCloseListener {
                hideSearchBar()
                true
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        "onOptionsItemSelected called".logD(TAG)
        when (item.itemId) {
            android.R.id.home -> navController.popBackStack()
            R.id.menu_item_search -> showSearchBar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSearchBar() {
        searchView.visibility = View.VISIBLE
        searchBar.isVisible = true
        searchIcon.isVisible = false
        searchView.queryHint = searchHint
        searchView.requestFocus()
    }

    private fun hideSearchBar() {
        searchView.clearFocus()
        searchView.visibility = View.GONE
        searchBar.isVisible = false
        searchIcon.isVisible = true
    }

    override fun onSearchClick() {
        "search() called".logD(TAG)
        searchFunction?.invoke()
    }

    override fun setOnSearchClick(hintText: String, onSearchClick: () -> Unit) {
        "in setOnSearchClickListener".logD(TAG)
        searchHint = hintText
        searchFunction = onSearchClick
    }
}
