package info.androidhive.navigationdrawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.HomeFragment;
import info.androidhive.navigationdrawer.other.CircleTransform;

public class MainActivity extends AppCompatActivity {

    public static final String Database_Path = "All_Image_Uploads_Database";

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image and profile image
    private static final String urlNavHeaderBg = "https://educrib.sirv.com/uploads/cc518737bb9a8370e455c8ca801fd621St.AloysiusCollege_Cover.jpg";
    private static final String urlProfileImg = "data:image/png;base64,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";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        //txtName.setText("Ravi Tamada");
        //txtWebsite.setText("www.androidhive.info");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {

        return new HomeFragment();

    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                int id = menuItem.getItemId();

                if (id == R.id.nav_about_us) {
                    startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_privacy_policy) {
                    startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_pdf) {
                    startActivity(new Intent(MainActivity.this, PdfActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(MainActivity.this, ChatStartActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_news) {
                    startActivity(new Intent(MainActivity.this, NewsActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_events) {
                    startActivity(new Intent(MainActivity.this, EventsActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_notifications) {
                    startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                    drawer.closeDrawers();
                    return true;
                } else {
                    navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}