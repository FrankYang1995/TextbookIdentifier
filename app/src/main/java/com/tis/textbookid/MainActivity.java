package com.tis.textbookid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void ClickCam(View view)
    {
        int viewID = view.getId();
        if(viewID == R.id.btnCam)
        {
            startCamera();
        }
    }

    public void startCamera()
    {
        Intent intent = new Intent(this, camera.class);
        startActivity(intent);
    }

    public void upload(View view)
    {
        //upload Image
    }
    public void getResult(View view){
        Ion.with(this)
                .load("http://www.brandonroberts.me/test.json")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        ArrayList<JSONObject> books_arrlist = new ArrayList<JSONObject>();

                        try {
                            JSONObject jsonres = new JSONObject(result);
                            JSONArray books = jsonres.getJSONArray("books");

                            if (books != null) {
                                for (int i=0;i<books.length();i++){
                                    books_arrlist.add(books.getJSONObject(i));
                                }
                            }

                        } catch (JSONException jsone) {
                            Log.wtf("json error jsone: ", jsone);

                        }

                        displayData(books_arrlist);;
                    }
                });
    }



    public class BooksAdapter extends ArrayAdapter<JSONObject> {
        public BooksAdapter(Context context, ArrayList<JSONObject> books) {
            super(context, 0, books);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            JSONObject book = getItem(position);

            String title = "";
            String author = "";
            String isbn = "ISBN: ";
            String pdf = "";
            String amazon = "";
            String image  = "http://www.iconarchive.com/download/i99738/sonya/swarm/Nerd-Glasses.ico";

            try {
                title = book.getString("title");
                author = book.getString("author");
                isbn += book.getString("ISBN");
                pdf = book.getString("download");
                amazon = book.getString("amazon_link");
                image = book.getString("image");

            } catch (JSONException je){
                Log.wtf("json error in displayData: ", je);
            }

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookresult, parent, false);
            }
            // Lookup view for data population
            TextView tv_isbn = (TextView) convertView.findViewById(R.id.res_isbn);
            TextView tv_title = (TextView) convertView.findViewById(R.id.res_title);
            TextView tv_author = (TextView) convertView.findViewById(R.id.res_author);
            ImageView tv_img = (ImageView) convertView.findViewById(R.id.res_img);

            final String pdflink = pdf;
            ImageButton tv_pdf = (ImageButton) convertView.findViewById(R.id.res_pdf);
            tv_pdf.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = pdflink;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            final String amalink = amazon;
            ImageButton tv_amazon = (ImageButton) convertView.findViewById(R.id.res_amazon);
            tv_amazon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = amalink;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });


            //TextView tv_amazon = (TextView) convertView.findViewById(R.id.res_amazon);
            //tv_amazon.setClickable(true);
            //tv_amazon.setMovementMethod(LinkMovementMethod.getInstance());
            //String amazon_text = "<a href='" + amazon + "'> Amazon </a>";
            //tv_amazon.setText(Html.fromHtml(amazon_text));


            // Populate the data into the template view using the data object
            tv_isbn.setText(isbn);
            tv_title.setText(title);
            tv_author.setText(author);

            new DownloadImageTask(tv_img).execute(image);
            return convertView;
        }
    }

    private void displayData(ArrayList<JSONObject> books_arrlist) {
        BooksAdapter adapter = new BooksAdapter(this, books_arrlist);
        ListView listView = (ListView) findViewById(R.id.reslist);
        listView.setAdapter(adapter);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView cmImage) {
            this.bmImage = cmImage;
        }

        protected Bitmap doInBackground(String...urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
