package com.example.santhosh.materialnews;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.santhosh.materialnews.model.moviemodel;
import com.squareup.picasso.Picasso;
import com.google.gson.Gson;

import org.json.JSONArray;
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

public class MainActivity extends Activity {
    private ListView list;
    private final String ur = "https://newsapi.org/v1/articles?source=fortune&sortBy=top&apiKey=41946d46c68c429896630a6efb400619";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.a,null));
        dialog.setTitle("Loading News");
        dialog.setMessage("Please wait!!");

        list = (ListView) findViewById(R.id.list);
        new jsontask().execute(ur);
    }


    public class jsontask extends AsyncTask<String, String, List<moviemodel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<moviemodel> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader buff = null;
            String store;
            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                buff = new BufferedReader(new InputStreamReader(stream));
                StringBuffer temp = new StringBuffer();
                String line = "";
                while ((line = buff.readLine()) != null) {
                    temp.append(line);
                }
                store = temp.toString();
                JSONObject parentObject = new JSONObject(store);
                JSONArray array = parentObject.getJSONArray("articles");
                List<moviemodel> lst = new ArrayList<>();
                Gson gson = new Gson();


                for (int i = 0; i < array.length(); i++) {
                    JSONObject finalobject = array.getJSONObject(i);
                    moviemodel mod = gson.fromJson(finalobject.toString(), moviemodel.class);
/**

 mod.setStatus(parentObject.getString("status"));
 mod.setSource(parentObject.getString("source"));
 mod.setSortBy(parentObject.getString("sortBy"));
 mod.setAuthor(finalobject.getString("author"));
 mod.setTitle(finalobject.getString("title"));
 mod.setDescription(finalobject.getString("description"));
 mod.setUrl(finalobject.getString("url"));
 mod.setUrlToImage(finalobject.getString("urlToImage"));
 mod.setPublishedAt(finalobject.getString("publishedAt"));
 **/
                    lst.add(mod);

                }

                return lst;
            } catch (MalformedURLException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (buff != null) {
                        buff.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<moviemodel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                adapter adap = new adapter(getApplicationContext(), R.layout.row, result);
                list.setAdapter(adap);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        moviemodel mod = result.get(position); // getting the model
                        Intent intent = new Intent(MainActivity.this, next.class);
                        intent.putExtra("movieModel", new Gson().toJson(mod)); // converting model json into string type and sending it via intent
                        startActivity(intent);
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url:)", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class adapter extends ArrayAdapter {
        private List<moviemodel> mm;
        private int resource;
        private LayoutInflater inflater;
        private String tempo;

        public adapter(Context context, int resource, List<moviemodel> objects) {
            super(context, resource, objects);
            mm = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);


                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.title = (TextView) convertView.findViewById(R.id.author);
                holder.author = (TextView) convertView.findViewById(R.id.title);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tempo = mm.get(position).getUrlToImage();
            holder.title.setText(mm.get(position).getTitle());
            holder.author.setText("Author: " + mm.get(position).getAuthor());
            holder.date.setText("Pub.Dt:" + mm.get(position).getPublishedAt());
            Picasso.with(getApplicationContext()).load(holder.tempo).into(holder.iv);



            return convertView;
        }


        class ViewHolder {
            private String tempo;
            private ImageView iv;
            private TextView title;
            private TextView author;
            private TextView date;
        }
    }

}
