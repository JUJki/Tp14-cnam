package com.form.tp14;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by julien on 21/06/2016.
 */
public class GCMListRegIds implements Iterable<String> {

    public static final String LIST_NAME = "listeSMB116";  // c'est le nom de la liste globale, partagée par tous

    private static final boolean I = true;

    private String name;
    private String urlServer;
    private Context context;

    public GCMListRegIds(Context context, String name, String urlServer) {
        this.name = name;
        this.urlServer = urlServer;
        this.context = context;
    }

    public GCMListRegIds(Context context, String name) {
        this(context, name, "http://jfod.cnam.fr/jnews/tests/http_list.html");
        //this(context, name, "http://10.0.2.2:8799/http_list.html");
        // 10.0.2.2 (localhost) le service est sur la meme machine que l'emulateur
    }

    public String getName() {
        return name;
    }

    public List<String> regIds() {
        return new ArrayList<String>(stringToSet(toString()));
    }

    public String toString() {
        String parameters = "?nom=" + name + "&commande=toString";
        Connexion connect = new Connexion(urlServer, parameters);
        String result = "[]";
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                result = connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            } else {
                result = connect.execute().get();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private static class ThreadConnexion extends Thread {
        private String url;
        private String parametres;
        private String result;

        public ThreadConnexion(String url, String parametres) {
            this.url = url.trim() + parametres.trim();
            this.result = new String("");
            this.start();
        }

        public String get() {
            try {
                this.join();
            } catch (InterruptedException e) {
                //ie.printStackTrace();
                throw new RuntimeException(e.getCause());
            }
            if (I) Log.i("GCM_ThreadConnexion: ", result);
            return result;
        }

        public void run() {
            HttpURLConnection connection = null;
            try {
                URL urlConnection = new URL(url);
                connection = (HttpURLConnection) urlConnection.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 1024);
                String inputLine = in.readLine();
                while (inputLine != null) {
                    result += inputLine;
                    inputLine = in.readLine();
                }
                in.close();
            } catch (Exception e) {
                this.result = "";
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        }
    }


    private static class Connexion extends AsyncTask<String, Void, String> {
        private String url;
        private String parametres;

        public Connexion(String url, String parametres) {
            this.url = url.trim() + parametres.trim();
        }

        @Override
        protected String doInBackground(String... unused) {
            //Log.i("GCMListRegIds, requete en :", this.url);
            StringBuffer result = new StringBuffer();
            HttpURLConnection connection = null;
            try {
                URL urlConnection = new URL(url);
                connection = (HttpURLConnection) urlConnection.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()), 1024);
                String inputLine = in.readLine();
                while (inputLine != null) {
                    result.append(inputLine);
                    inputLine = in.readLine();
                }
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return result.toString();
        }


    }
    private static Set<String> stringToSet(String str) {
        Set<String> set = new HashSet<String>(); // elimination des doublons, au cas ou ...
        StringTokenizer st = new StringTokenizer(str, "[], ");
        while (st.hasMoreTokens()) {
            String regId = st.nextToken();
            set.add(regId);
        }
        return set;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            Iterator<String> it = stringToSet(GCMListRegIds.this.toString()).iterator();
            String current;

            public boolean hasNext() {
                return it.hasNext();
            }

            public String next() {
                current = it.next();
                return current;
            }

            public void remove() { throw new UnsupportedOperationException();/* pas de remove ici, ou bien it.remove(); remove(current); */}
        };
    }
}
