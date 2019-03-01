package edu.rosehulman.dripchar.artpics

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PicListFragment.OnPicSelectedListener, SplashFragment.OnLoginButtonPressedListener {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var authListener : FirebaseAuth.AuthStateListener
    private var showMine = true


    private val RC_SIGN_IN = 1

    lateinit var floatingActionButton: FloatingActionButton
    private val titleRef = FirebaseFirestore
        .getInstance()
        .collection("data")
        .document("apptitle")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //TODO: Maybe it will only go to PicListFragment on launch?

        floatingActionButton = fab
        initializeListeners()

    }

    // add (and remove) an auth state listener upon start (and stop).
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private fun initializeListeners() {
        // TODO: Create an AuthStateListener that passes the UID
        // to the MovieQuoteFragment if the user is logged in
        // and goes back to the Splash fragment otherwise.
        // See https://firebase.google.com/docs/auth/users#the_user_lifecycle
        authListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            Log.d(Constants.TAG, "User: $user")
            if (user == null) {
                switchToSplashFragment()
            } else {
                switchToPicListFragment(user.uid)
            }
        }

    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun switchToPicListFragment(uid: String) {
        val ft = supportFragmentManager.beginTransaction()
        Log.d(Constants.TAG, "$uid")
        val p = PicListFragment.newInstance(uid)
        p.showMine = showMine
        ft.replace(R.id.fragment_container, p)
        ft.commit()
    }

    override fun onLoginButtonPressed() {
        launchLoginUI()
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                true
            }

            R.id.action_change_title -> {
                showChangeTitleDialog()
                true
            }
            R.id.action_show -> {
                if(showMine) {
                    item.setTitle("SHOW ALL")
                } else {
                    item.setTitle("SHOW MINE")
                }
                showMine = !showMine

                //TODO: RESET PICLIST FRAGMENT????

                //Maybe a bug here?
                val p = PicListFragment.newInstance(auth.currentUser!!.uid)
                p.showMine = showMine
                replaceFragment(p)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun launchLoginUI() {
        // TODO: Build a login intent and startActivityForResult(intent, ...)
        // For details, see https://firebase.google.com/docs/auth/android/firebaseui#sign_in

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        //TODO: Fix something here

// Create and launch sign-in intent
        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.goughheadlol)
            .build()

// Create and launch sign-in intent
        startActivityForResult(
            loginIntent,
            RC_SIGN_IN
        )

    }


    override fun onPicSelected(pic : Pic) {

        //NOTE: Is doing parcelable a bit overkill?
        Log.d(Constants.TAG, "onPicSelected being hit!")
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(
            R.id.fragment_container,
            PicDetailFragment.newInstance(pic),
            "about"
        )  //ft.add, adds on top if using frame layout
        floatingActionButton.hide()
        ft.addToBackStack("picList")
        ft.commit()
    }

    fun showChangeTitleDialog() {

        titleRef.get().addOnSuccessListener{ snapshot: DocumentSnapshot? ->
            //TODO: What if the app is running for the first time? Author is null...
            val title: String = (snapshot?.get("title") ?: "") as String
            val builder = AlertDialog.Builder(this)
            builder.setTitle("App Title")
            val titleEditText = EditText(this)
            titleEditText.hint = "App title's name"
            titleEditText.setText(title)
            builder.setView(titleEditText)
            builder.setPositiveButton("ok") {_, _ ->
                titleRef.set(mapOf(Pair("author", titleEditText.text.toString())))
                supportActionBar?.title = titleEditText.text.toString()
            }

            builder.setNegativeButton(android.R.string.cancel, null)

            builder.create().show()

        }
    }


    override fun onBackPressed() {
        floatingActionButton.show()
        super.onBackPressed()
    }

    override fun getFab() : FloatingActionButton {
        return floatingActionButton
    }
}
