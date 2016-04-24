package com.borombo.demo.storelocatordemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by Erwan on 24/04/2016.
 * Activité regroupant les fonctions communes pour les activité ayant le menu latéral
 */
public abstract class LateralMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Si le menu latéral est ouvert, on le ferme
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            // CLique sur "Site Web"
            case R.id.site_web :
                // Lance le navigateur avec la page Web
                String url = getString(R.string.website);
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
                startActivity(intent);
                break;
            // Clique sur "Mentions Légales"
            case R.id.mentions_legales :
                // Ouvre une Dialog avec les mentions légales
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.mentions))
                        .setMessage(R.string.mentions_legales)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            // CLique sur "Partager l'app"
            case  R.id.partager :
                // Permet de sélectionner l'application avec laquelle partager le lien du site web
                startActivity(Intent.createChooser(getWebShareItent(), getString(R.string.share_via)));
                break;
        }
        // On ferme le menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fonction qui permet de récupérer l'intent pour partager l'adresse web
     * @return
     */
    protected Intent getWebShareItent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.website));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

}
