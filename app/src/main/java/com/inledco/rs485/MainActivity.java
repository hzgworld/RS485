package com.inledco.rs485;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.inledco.rs485.device.MasterFragment;
import com.inledco.rs485.smartlink.SmartlinkFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout main_drawer;
    private Toolbar main_toolbar;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        main_toolbar = (Toolbar) findViewById( R.id.main_toolbar );
        if ( main_toolbar != null )
        {
            setSupportActionBar( main_toolbar );
        }

        main_drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if ( main_drawer != null )
        {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, main_drawer, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
            main_drawer.addDrawerListener( toggle );
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
    }

    @Override
    public void onBackPressed ()
    {
        if ( main_drawer != null && main_drawer.isDrawerOpen( GravityCompat.START ) )
        {
            main_drawer.closeDrawer( GravityCompat.START );
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings ( "StatementWithEmptyBody" )
    @Override
    public boolean onNavigationItemSelected ( MenuItem item )
    {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        switch ( item.getItemId() )
        {
            case R.id.nav_device:
                if ( main_toolbar != null )
                {
                    main_toolbar.setTitle( item.getTitle() );
                }
                beginTransaction.replace( R.id.main_fl_show, new MasterFragment() ).commit();
                break;

            case R.id.nav_smartlink:
                if ( main_toolbar != null )
                {
                    main_toolbar.setTitle( item.getTitle() );
                }
                beginTransaction.replace( R.id.main_fl_show, new SmartlinkFragment() ).commit();
                break;

            case R.id.nav_settings:

                break;

            case R.id.nav_about:

                break;
        }
        if ( main_drawer != null )
        {
            main_drawer.closeDrawer( GravityCompat.START );
        }
        return true;
    }
}
