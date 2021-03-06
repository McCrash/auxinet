package com.vpfc18.vpfc18.Principal.Voluntario.Principal;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.vpfc18.vpfc18.Adaptadores.LVAdapterAlertas;
import com.vpfc18.vpfc18.Base_de_datos.MapsAPI;
import com.vpfc18.vpfc18.Base_de_datos.respuestaMapa;
import com.vpfc18.vpfc18.Entidades.Datos_Alertas;
import com.vpfc18.vpfc18.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class Voluntario_Listado_Fragment extends Fragment implements OnMapReadyCallback{
    private SwipeRefreshLayout sr_voultario_listado;

    LVAdapterAlertas adaptador;
    private String correoUser;
    ListView lv_lista_voluntario_listado;
    private double longitudAsistente,latitudAsistente;
    private LatLng actual;

    ArrayList<Datos_Alertas> datos_alertas;
    Datos_Alertas eAlertas = new Datos_Alertas();

    View mView;
    private GoogleMap mGoogleMaps;
    private MapView mMapView;
    private Boolean salir = false;

    public Voluntario_Listado_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.voluntario_fragment_listado_, container, false);

        correoUser = getArguments().getString("correoUser");
        lv_lista_voluntario_listado = (ListView) mView.findViewById(R.id.lv_lista_voluntario_listado);
        lv_lista_voluntario_listado.setClickable(true);
        lv_lista_voluntario_listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                verAsistido(position);
            }
        });
        return mView;
    }
    public void verAsistido(int num){
        String nombreAsistido = datos_alertas.get(num).getNombreAsistido();
        String idCorreoAsistido = datos_alertas.get(num).getId_asistente();
        String telefonoAsistido = datos_alertas.get(num).getTelefono();
        int idAlerta = datos_alertas.get(num).getId_alerta();
        Fragment fragmentoSeleccionado = new Voluntario_Detalle_Fragment_1();
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.voluntario_contenedor_principal, fragmentoSeleccionado);
        t.commit();
        Bundle datos = new Bundle();
        datos.putInt("idAlerta", idAlerta);
        datos.putString("nombreAsistido", nombreAsistido);
        datos.putString("correoUser", correoUser);
        datos.putString("idCorreoAsistido",idCorreoAsistido);
        datos.putString("telefonoAsistido",telefonoAsistido);
        datos.putString("viajando","listado");
        datos.putDouble("latitudAsistente",latitudAsistente);
        datos.putDouble("longitudAsistente",longitudAsistente);
        Double latitudAsistido = datos_alertas.get(num).getLatitud();
        Double longitudAsistido = datos_alertas.get(num).getLongitud();
        datos.putDouble("latitudAsistido",latitudAsistido);
        datos.putDouble("longitudAsistido",longitudAsistido);
        fragmentoSeleccionado.setArguments(datos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        mMapView.setVisibility(View.INVISIBLE);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMaps = googleMap;
        mGoogleMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMaps.clear();
        mGoogleMaps.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMaps.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMaps.getUiSettings().setZoomControlsEnabled(true);

        mMapView.setVisibility(View.INVISIBLE);

        setMyLocationEnabled();

    }

    private void setMyLocationEnabled() {
        mGoogleMaps.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMapView.setVisibility(View.INVISIBLE);
        mGoogleMaps.setMyLocationEnabled(true);
        mGoogleMaps.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (salir == false) {
                    //------------posicion de mi marcador--------------//
                    latitudAsistente = location.getLatitude();
                    longitudAsistente = location.getLongitude();
                    actual = new LatLng(latitudAsistente, longitudAsistente);
                    salir = true;
                    cargarAlertasMapa();
                }
            }
        });
    }

    public void cargarAlertasMapa() {
        MapsAPI mapsAPI = new MapsAPI(new respuestaMapa<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                datos_alertas = null;

                datos_alertas = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject object = response.getJSONObject(i);
                        String id_dependiente = object.getString("id_dependiente");
                        int id_alerta = object.getInt("id_alerta");
                        String nombreAsistidoDetalle = object.getString("nombre");
                        double latitudAsistido = object.getDouble("latitud");
                        double longitudAsistido = object.getDouble("longitud");
                        String telefono = object.getString("telefono");
                        String tipoAlerta = object.getString("nombreAlerta");

                        // int distancia = (int) calcularDistancia(latitudAsistido, longitudAsistido);
                        int distancia = 0;
                        eAlertas = new Datos_Alertas(id_alerta, nombreAsistidoDetalle, latitudAsistido, longitudAsistido, telefono, tipoAlerta, distancia,id_dependiente);
                        datos_alertas.add(eAlertas);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        //posicionAsistidos();
                    }
                }
                adaptador = new LVAdapterAlertas(datos_alertas, getContext(), getActivity().getFragmentManager(), correoUser,latitudAsistente,longitudAsistente);
                lv_lista_voluntario_listado.setAdapter(adaptador);

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mapsAPI.cargarAlertas();
    }
}