package com.yesinc.tsi880;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.yesinc.tsi880.models.SondeModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;


public class TelemetryActivity extends AppCompatActivity {

    private TextView tvData;
    private ListView lvSondes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry); //list view
        ListView lvSondes = findViewById(R.id.lvSondes);  //each node of list

    }

    public class JSONTask extends AsyncTask<String, String, List<SondeModel> >{

        @Override
        protected List<SondeModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line;
                while((line=reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONObject parentArray0 = parentObject.getJSONObject("0"); //first set of sondes in sigstr2
                JSONObject parentArray1 = parentObject.getJSONObject("1"); //second set of sondes in sigstr2

                List<SondeModel> sondeModelList = new ArrayList<>();

                for(int i=0; i<8; i++){ //populates sondeModel with parentArray0

                    String sonde = String.valueOf(i);
                    JSONObject finalObject = parentArray0.getJSONObject(sonde);
                    SondeModel sondeModel = new SondeModel();
                    sondeModel.setNoisedbm(finalObject.getString("noisedbm"));
                    sondeModel.setSigdbm(finalObject.getString("sigdbm"));
                    sondeModelList.add(sondeModel);
                }

                for(int i=0; i<8; i++){  //populates sondeModel with parentArray1

                    String sonde = String.valueOf(i);
                    JSONObject finalObject = parentArray1.getJSONObject(sonde);
                    SondeModel sondeModel = new SondeModel();
                    sondeModel.setNoisedbm(finalObject.getString("noisedbm"));
                    sondeModel.setSigdbm(finalObject.getString("sigdbm"));
                    sondeModelList.add(sondeModel);
                }
                return sondeModelList; //returns array list, sondeModel

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SondeModel> result) {
            //should populate ListView in UI but isn't working
            super.onPostExecute(result);

            SondeAdapter adapter = new SondeAdapter(getApplicationContext(), R.layout.row, result);
            //lvSondes.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //refreshes telemetry or goes to home/help
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.navigation_telemetry) {
            new JSONTask().execute("http://172.16.5.70/plots/sigstr2.txt");
            return true;
        } else if (id == R.id.navigation_home) {
            Intent intent = new Intent(TelemetryActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.navigation_help) {
            Intent h_intent = new Intent(TelemetryActivity.this, HelpActivity.class);
            startActivity(h_intent);
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }

    }

    public class SondeAdapter extends ArrayAdapter{
        public List<SondeModel> sondeModelList;
        private int resource;
        private LayoutInflater inflater;
        public SondeAdapter(Context context, int resource, List<SondeModel> objects) {
            super(context, resource, objects);
            sondeModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource, null);
            }

            TextView sig;
            TextView noise;

            sig = convertView.findViewById(R.id.sig);
            noise = convertView.findViewById(R.id.noise);

            sig.setText(sondeModelList.get(position).getSigdbm());
            noise.setText(sondeModelList.get(position).getNoisedbm());

            return convertView;
        }
    }

}
