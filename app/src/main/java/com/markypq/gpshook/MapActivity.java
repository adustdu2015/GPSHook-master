package com.markypq.gpshook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;



public class MapActivity extends AppCompatActivity implements AMap.OnMapClickListener {

    private MapView mv;
    private AMap aMap;
    private LatLng latLng;
    private String pacakgeName;
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pacakgeName = getIntent().getStringExtra("package_name");

        mv = (MapView) findViewById(R.id.mv);
        assert mv != null;
        mv.onCreate(savedInstanceState);
        aMap = mv.getMap();
        Intent intent =getIntent();
        double lat =   intent.getDoubleExtra("lan",0);
        double lon = intent.getDoubleExtra("lon",0);
        LatLng latLng1 = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng1);
        markerOptions.draggable(true);
        markerOptions.title("经度：" + latLng1 .longitude + ",纬度：" + latLng1 .latitude);
        aMap.addMarker(markerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng1));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel()));
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setOnMapClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (latLng == null) {
                    Toast.makeText(this, "请点击地图选择一个地点！", Toast.LENGTH_SHORT).show();
                    return true;
                }

                save();
                break;
            case R.id.search:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null, false);
                final EditText et_key = (EditText) view.findViewById(R.id.key);
                new AlertDialog.Builder(this).setView(view)
                        .setTitle("搜索位置")
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                search(et_key.getText().toString());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    private void search(final String key) {
        PoiSearch.Query query = new PoiSearch.Query(key, null, null);
        query.setPageSize(10);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i == 1000) {
                    final ArrayList<PoiItem> poiItems = poiResult.getPois();
                    if (poiItems.size() != 0) {
                        String[] keyList = new String[poiItems.size()];
                        for (int j = 0; j < poiItems.size(); j++) {
                            keyList[j] = poiItems.get(j).getTitle();
                        }
                        new AlertDialog.Builder(MapActivity.this)
                                .setTitle("选择位置")
                                .setSingleChoiceItems(keyList, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(poiItems.get(which).getLatLonPoint().getLatitude(), poiItems.get(which).getLatLonPoint().getLongitude())));
                                        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel()));
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        Toast.makeText(MapActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        aMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("经度：" + latLng.longitude + ",纬度：" + latLng.latitude);
        aMap.addMarker(markerOptions);
        this.latLng = latLng;
    }

    public void save() {
        Intent intent =new Intent();
        intent.putExtra("lan",latLng.latitude);
        intent.putExtra("lon",latLng.longitude);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
