package com.form.tp14;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

public class MainActivity extends AppCompatActivity {

    // le message à envoyer
    private EditText et;
    // La liste des abonnés
    private GCMListRegIds listRegIds;
    // Envoi vers le cloud
    private Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.et = (EditText) findViewById(R.id.messageId);
        et.requestFocus();
        this.sender = new Sender(MainActivity.this.getString(R.string.api_key));
        this.listRegIds = new GCMListRegIds(this, GCMListRegIds.LIST_NAME); // liste partagée par tous
    }
}
