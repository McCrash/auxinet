package com.vpfc18.vpfc18.Principal.Voluntario.Principal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vpfc18.vpfc18.Entidades.Datos_Alertas;
import com.vpfc18.vpfc18.Principal.Voluntario.Perfil.Voluntario_Perfil_Fragment;
import com.vpfc18.vpfc18.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class Voluntario_Principal_Activity extends AppCompatActivity {

    android.support.v7.widget.Toolbar voluntario_principal_toolbar;
    int contador=0;
    int perfil = 0;
    int STORAGE_PERMISSION_CODE=2;
    String correoUser;
    ArrayList<Datos_Alertas> datos_alertas;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voluntario_activity_principal);

        correoUser = getIntent().getStringExtra("correoUser");
        voluntario_principal_toolbar =(android.support.v7.widget.Toolbar) findViewById(R.id.voluntario_principal_toolbar);
        setSupportActionBar(voluntario_principal_toolbar);
        voluntario_principal_toolbar.setNavigationIcon(R.drawable.ic_perfil);
        cargaMapa();
        voluntario_principal_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perfil==0){
                    cargaPerfil();
                }else{
                    cargaMapa();
                }

            }
        });
    }

    //Para poder colocar el menu en la action bar, tenemos que añadir este método e inflar el recurso
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barra_superior,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Para añadir el método cuando presione el menu derecho
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_derecho:
                if (contador==0){
                    contador=1;
                    item.setIcon(R.drawable.ic_map);
                    cargaLista();
                }else{
                    contador=0;
                    item.setIcon(R.drawable.ic_notificacion);
                    cargaMapa();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargaMapa(){
        perfil=0;
        requestStoragePermission();
        Fragment fragmentoSeleccionado = new Voluntario_Mapa_Fragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.voluntario_contenedor_principal, fragmentoSeleccionado);
        t.commit();
        Bundle datos = new Bundle();
        datos.putString("correoUser", correoUser);
        fragmentoSeleccionado.setArguments(datos);
    }
    private void cargaLista(){
        perfil=0;
        voluntario_principal_toolbar.setNavigationIcon(R.drawable.ic_perfil);
        getSupportActionBar().setTitle("Listado eventos");
        Fragment fragmentoSeleccionado = new Voluntario_Listado_Fragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.voluntario_contenedor_principal, fragmentoSeleccionado);
        t.commit();
        Bundle datos = new Bundle();
        datos.putString("correoUser", correoUser);
        fragmentoSeleccionado.setArguments(datos);
    }
    private void cargaPerfil(){
        perfil=1;
        voluntario_principal_toolbar.setNavigationIcon(R.drawable.ic_vacio);
        getSupportActionBar().setTitle("Perfil Usuario");
        Fragment fragmentoSeleccionado = new Voluntario_Perfil_Fragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.voluntario_contenedor_principal, fragmentoSeleccionado);
        t.commit();
        Bundle datos = new Bundle();
        datos.putString("correoUser", correoUser);
        fragmentoSeleccionado.setArguments(datos);
    }

    public void requestStoragePermission() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voluntario_principal_toolbar.setNavigationIcon(R.drawable.ic_perfil);
                getSupportActionBar().setTitle("Mapa eventos");
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Voluntario_Principal_Activity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
            }
        }
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gohome();
            } else {

            }
        }
    }
    public void gohome(){
        Thread t = new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                try {
                    sleep(700);
                }catch (Exception e){

                } finally {
                    cargaMapa();
                }
            }
        };
        t.start();
    }
}

