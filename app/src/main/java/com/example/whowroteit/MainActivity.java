package com.example.whowroteit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
//Deklarasi Variable untuk id pada layout
public class MainActivity extends AppCompatActivity {
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Menginisiasikan variable dengan string pada layout
        setContentView(R.layout.activity_main);
        mBookInput=(EditText)findViewById(R.id.bookInput);
        mTitleText=(TextView)findViewById(R.id.titleText);
        mAuthorText=(TextView)findViewById(R.id.authorText);

    }

    public void searchBooks(View view) {
        //Membuat fungsi mengambil string dari api

        String queryString=mBookInput.getText().toString();
        //Menyembunyikan keyboard setelah di push
        InputMethodManager inputManager =(InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        //Pengecekan Status Internet
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Memeriksa jika lancar maka class fetchbook akan di execute
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0){
            new FetchBook(mTitleText,mAuthorText,mBookInput).execute(queryString);
        }//Koneksi apabila tidak lancer
        else {
            if (queryString.length()==0){
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_search_term);

            } else {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }
    }
}
