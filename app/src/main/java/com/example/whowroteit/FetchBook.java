package com.example.whowroteit;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
//Class Fetchbook keturunan dari asynctask yaitu mengimport variabel dari kelas mainactivity
public class FetchBook extends AsyncTask<String, Void, String> {
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;
    private static final String LOG_TAG= FetchBook.class.getSimpleName();
    //Membuat method untuk menginisiasi variabel di mainactivity
    public FetchBook(TextView titleText , TextView authorText, EditText bookInput) {
        this.mTitleText=titleText;
        this.mAuthorText=authorText;
        this.mBookInput=bookInput;
    }
//Fungsi untuk mengambil sumber buku dari URL google API
    @Override
    protected String doInBackground(String... params) {
        String queryString = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null ;
        String bookJSONString = null;
        //Menginisiasi url sumber buku
        try {
            final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String MAX_RESULTS = "maxResults";
            final String PRINT_TYPE = "printType";

            //Membuat uri untuk google api
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS,"10")
                    .appendQueryParameter(PRINT_TYPE,"books")
                    .build();
            //Merequest url yang telah di deklarasikan
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream =urlConnection.getInputStream();

            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line + "\n");
            }

            if (builder.length() == 0){
                return null;
            }
            bookJSONString = builder.toString();

        }catch (IOException e) {
            e.printStackTrace();
        //Yang terjadi ketika telah selesai akan disconect dari internet
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader !=null){
                try {
                    reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return bookJSONString;

    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject =new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i =0 ;
            String title = null;
            String authors = null;

            while (i < itemsArray.length() || (authors == null && title == null)){
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeinfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeinfo.getString("title");
                    authors = volumeinfo.getString("authors");
                } catch (Exception e){
                    e.printStackTrace();
                }

                i++;
            }
           if (title != null && authors != null){
               mTitleText.setText(title);
               mAuthorText.setText(authors);
               mBookInput.setText("");
           }else {
               mTitleText.setText(R.string.no_results);
               mAuthorText.setText("");
           }

        } catch (Exception e){
            mTitleText.setText(R.string.no_results);
            mAuthorText.setText("");
            e.printStackTrace();
        }
    }
}
