package app.gotogether.com.mapactivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class MapsActivity extends AppCompatActivity {



    ArrayAdapter<String> adapter;
    ArrayList<String> as;
    //ArrayList<DayData> dayData;
    ListView lv;
    ArrayList<String> team_list; // 팀리스트 배열
    ListView team_lv; // 팀리스트 리스트뷰
    ArrayList<String> phone_list;

    private static String TAG4 = "memberlist_json";

    private static final String TAG_JSON = "gps_json";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LONG = "longitude2";
    private static final String TAG_LATI = "latitude2";
    private static final String TAG_JSON3="memberlist_json";
    private static final String TAG_GroupID3 = "group_id";
    private static final String TAG_UID = "uid";
    private static final String TAG_Name = "name";
    private static final String TAG_Position ="position";
    private static final String TAG_Phone ="phone";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    HashMap<String, String> hashMap = new HashMap<>();

    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    AlertDialog.Builder builder;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String value, value2;
    Dialog dig;
    double latitude, longitude;
    private LocationManager locationmanager;
    MarkerOptions myLocationMarker;
    MarkerOptions friendMarker[] = new MarkerOptions[2];
    MarkerOptions friendMarker2;
    String longitude1[];
    String latitude1[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mArrayList = new ArrayList<>();



        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;

            }
        });


        configure_button();
        requestMyLocation();
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, new LocationListener() {
            @Override

            public void onLocationChanged(Location location) {
                String schedule_id = "3";
                String id = "sss";
                String name = "yeon";
                String longitude2 = Double.toString(location.getLongitude());
                String latitude2 = Double.toString(location.getLatitude());
                Toast.makeText(MapsActivity.this,longitude2+latitude2,Toast.LENGTH_LONG).show();
                InsertData task  = new InsertData();
                task.execute(schedule_id,id,name,longitude2,latitude2);

                GetData task2 = new GetData();
                task2.execute("http://211.253.9.84/getgps.php");
                for(int k = 0; k <= mArrayList.size(); k++) {
                    longitude1[k] = Double.toString(mArrayList.indexOf(3));
                    latitude1[k] = Double.toString(mArrayList.indexOf(4));
                }
            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);


                startActivity(intent);

                Toast.makeText(MapsActivity.this, "Gps is turned off!! ",

                        Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getBaseContext(), "Gps is turned on!! ",

                        Toast.LENGTH_SHORT).show();

            }
            @Override

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });


    }
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String... params) {
            String serverURL;
            String schedule_id = (String) params[0];
            String id = (String) params[1];
            String name = (String) params[2];
            String longitude2 = (String) params[3];
            String latitude2 = (String) params[4];



            serverURL = "http://211.253.9.84/insertgps.php";

            //서버에 들어가는 주소 ""안에 있는 변수이름 절대 변경 금지!
            String postParameters = "schedule_id=" + schedule_id + "&id=" + id + "&name=" + name + "&longitude=" + longitude2 + "&latitude=" + latitude2;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
    }


    private void requestMyLocation() {
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 10000;
            float minDistance = 1;

            locationmanager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            try {
                                showCurrentLocation(location);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                addPictures(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }

                    }
            );

            Location lastLocation = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

            locationmanager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10000,
                    1,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            try {
                                showCurrentLocation(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                addPictures(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }

            );


        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) throws JSONException {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        showMyLocationMarker(location);
        addPictures(location);
    }

    private void showMyLocationMarker(Location location) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myLocationMarker.title("내위치");
            myLocationMarker.snippet("내위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.dial));
            map.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void addPictures(final Location location) throws JSONException {
        int pictureResId = R.mipmap.ic_start;


        if(mJsonString != ""){
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String schedule_id = item.getString("schedule_id");
                String id = item.getString("id");
                String name = item.getString("name");
                String longitude = item.getString("longitude");
                String latitude = item.getString("latitude");
                //상대방 위치 띄우는 코드
                friendMarker[i] = new MarkerOptions();
                friendMarker[i].icon(BitmapDescriptorFactory.fromResource(pictureResId));
                friendMarker[i].position(new LatLng(Double.valueOf(longitude), Double.valueOf(latitude )));
                friendMarker[i].icon(BitmapDescriptorFactory.fromResource(pictureResId));
                map.addMarker(friendMarker[i]);

            }
        }


//            friendMarker1 = new MarkerOptions();
//            friendMarker1.position(new LatLng(longitude3, latitude3));
//            friendMarker1.title("친구 1\n");
//            friendMarker1.icon(BitmapDescriptorFactory.fromResource(pictureResId));
//           map.addMarker(friendMarker1);
//        } else {
//            friendMarker1.position(new LatLng(location.getLatitude() + 3000, location.getLongitude() + 3000));
//        }

//        pictureResId = R.mipmap.ic_end;
//
//            if (friendMarker2 == null) {
//
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject item = jsonArray.getJSONObject(i);
//                    String schedule_id = "3";
//                    String id = "sy";
//                    String name = "soyeon";
//                    String longitude4 = Double.toString(37.504913);
//                    String latitude4 = Double.toString(127.036690);
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put(TAG_ID, id);
//                    hashMap.put(TAG_NAME, name);
//                    hashMap.put(TAG_LONG, valueOf(longitude4));
//                    hashMap.put(TAG_LATI, valueOf(latitude4));
//                    mArrayList.add(hashMap);
//                }
//            }
        }

//            friendMarker2 = new MarkerOptions();
//            friendMarker2.position(new LatLng(37.504913, 127.036690));
//            friendMarker2.title("친구 2\n");
//            friendMarker2.icon(BitmapDescriptorFactory.fromResource(pictureResId));
//            map.addMarker(friendMarker2);
//        } else {
//            friendMarker2.position(new LatLng(location.getLatitude() + 2000, location.getLongitude() - 1000));
//        }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_dial:
                builder = new AlertDialog.Builder(this);
                String data[] = {"tel:01020891228","tel:01020891228","01000001201201","02103024929"};
                builder.setTitle("전화 걸 사람은??")
                        .setSingleChoiceItems(data,1, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .setNegativeButton("닫기",null)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), ",확인을눌렀습니다.",Toast.LENGTH_LONG).show();
                            }
                        });

                builder.setPositiveButton("닫기",null);
                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;

            case R.id.add_sos:
                builder = new AlertDialog.Builder(this);

                final String data1[] = {"유민","소연","슬기","예슬"};
                final boolean check[] = {true,false,true,false};
                builder.setTitle("문자 걸 사람 추가(최대 3명)")
                        .setMultiChoiceItems(data1, check, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                check[i] = b;
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .setNegativeButton("닫기",null)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String result = "";
                                for(int j = 0; j < check.length; j++) {
                                    if(check[j] == true) {
                                        result += " ," + data1[j];
                                    }
                                }
                                Toast.makeText(getApplicationContext(), result.substring(1),Toast.LENGTH_LONG).show();
                            }

                        });

                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
            case R.id.button_sos:
                String message = "sos! 위도: " + latitude + "경도: " + longitude;
                String tel = "01020891228";
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

                } else {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(tel, null, message, null, null);
                }
                break;
            case R.id.button_dial:
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:01020891228"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //request permission from user if the app hasn't got the required permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                            10);
                    return;
                }else {     //have got permission
                    try{
                        startActivity(i);  //call activity and make phone call
                    }
                    catch (android.content.ActivityNotFoundException ex){
                        Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.button_start:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS승인");
                builder.setMessage("GPS를 키시겠습니까?");
                builder.setIcon(R.mipmap.ic_start);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        requestMyLocation();
                        String schedule_id = "01";
                        String id = "sss";
                        String name = "yeon";
                        String longitude2 = Double.toString(longitude);
                        String latitude2 = Double.toString(latitude);

                        InsertData task = new InsertData();

                        task.execute(schedule_id, id, name, longitude2, latitude2); // 순서 꼭 이순서로 해줘~


                    }
                });
                builder.setNegativeButton("취소", null);

                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
            case R.id.button_end:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS거부");
                builder.setMessage("GPS를 끄시겠습니까?");
                builder.setIcon(R.mipmap.ic_end);
                builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestMyLocation();

                    }

                });

                builder.setNegativeButton("취소", null);

                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }
    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapsActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String longitude = item.getString(TAG_LONG);
                String latitude = item.getString(TAG_LATI);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_LONG, longitude);
                hashMap.put(TAG_LATI, latitude);

                mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
    private class GetData3 extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;




        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapsActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG4, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult3();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG4, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG4, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult3(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON3);

            team_list = new ArrayList<String>();
            phone_list = new ArrayList<String>();

            int member_num = 0;
            String boss_name = "";

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String group_id = item.getString(TAG_GroupID3);
                String uid = item.getString(TAG_UID);
                String name = item.getString(TAG_Name);
                String position = item.getString(TAG_Position);
                String phone = item.getString(TAG_Phone);

                if(group_id.equals("1")){
                    if(position.equals("1")){
                        boss_name = name;
                    }
                    team_list.add(name); // 추후에 팀 그룹 id가 같을시로 수정
                    phone_list.add(phone);
                    member_num ++;
                }


            }

            // 팀 수와 팀장 이름 갖고오기

            TextView textNum = (TextView)findViewById(R.id.textNum);
            textNum.setText(valueOf(member_num));

            TextView bossName = (TextView)findViewById(R.id.bossName);
            bossName.setText(boss_name);


        } catch (JSONException e) {
        }

    }

}




