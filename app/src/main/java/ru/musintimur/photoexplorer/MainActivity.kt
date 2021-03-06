package ru.musintimur.photoexplorer

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.network.checkExceptionType
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity()
    , OnSearchClick
    , NetworkCallback {
    private lateinit var navController: NavController
    private var searchFunction: (() -> Unit)? = null
    private var searchHint: String = ""
    private lateinit var searchView: SearchView
    private lateinit var searchIcon: MenuItem
    private lateinit var searchBar: MenuItem
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(
            Preferences.PREFERENCES.fileName,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        "onCreate called".logD(TAG)

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
            clearFocus()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        "onOptionsItemSelected called".logD(TAG)
        when (item.itemId) {
            android.R.id.home -> navController.popBackStack() //назад
            R.id.menu_item_search -> showSearchBar() //поиск
        }
        return super.onOptionsItemSelected(item)
    }

    //отображение поисковой панели
    private fun showSearchBar() {
        searchView.visibility = View.VISIBLE
        searchBar.isVisible = true
        searchIcon.isVisible = false
        searchView.queryHint = searchHint
        searchView.requestFocus()
    }

    //сокрытие поисковой панели
    private fun hideSearchBar() {
        searchView.clearFocus()
        searchView.visibility = View.GONE
        searchBar.isVisible = false
        searchIcon.isVisible = true
    }

    //запуск установленного механизма поиска и перехода в нужный фрагмент
    override fun onSearchClick() {
        searchFunction?.invoke()
    }

    //установка поискового механизма, использующегося во фрагменте
    override fun setOnSearchClick(hintText: String, onSearchClick: () -> Unit) {
        "in setOnSearchClickListener".logD(TAG)
        searchHint = hintText
        searchFunction = onSearchClick
    }

    //установка элементов интерфейса при удачном сетевом запросе
    override fun onSuccess() {
        "onSuccess called".logD(TAG)
        CoroutineScope(Dispatchers.Main).launch {
            layoutFragments.visibility = View.VISIBLE
            (includeError as TextView).run {
                visibility = View.GONE
                text = null
            }
        }
    }

    //отображение сообщения об ошибке
    override fun onError(e: Exception) {
        "onError called".logD(TAG)
        val errorMessage = checkExceptionType(e, TAG)
        errorMessage.logE(TAG)
        CoroutineScope(Dispatchers.Main).launch {
            layoutFragments.visibility = View.GONE
            (includeError as TextView).run {
                visibility = View.VISIBLE
                text = if (text.isNullOrBlank()) errorMessage else text
            }
        }
    }
}
