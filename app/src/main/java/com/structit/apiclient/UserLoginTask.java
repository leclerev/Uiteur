package com.structit.apiclient;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
    private static final String LOG_TAG = UserLoginTask.class.getSimpleName();

    private LoginActivity mActivity;
    private Document mDocument;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    public static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private final String mEmail;
    private final String mPassword;

    UserLoginTask(LoginActivity activity, String email, String password) {
        mActivity = activity;
        mEmail = email;
        mPassword = password;
        mDocument = null;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(mEmail)) {
                // Return null for XML document.
                return pieces[1].equals(mPassword);
            }
        }

        HttpURLConnection connection = null;

        String url = "http://uiteur.struct-it.fr/"
                + "login.php?user="
                + params[0]
                + "&pwd="
                + params[1];

        Log.d(LOG_TAG, "URL: " + url);

        try {
            URL serviceURL = new URL(url);
            connection = (HttpURLConnection) serviceURL.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i(LOG_TAG, "Log in granted...");

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine);
                }
                reader.close();

                Log.d(LOG_TAG, "Answer: " + buffer.toString());

                DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(buffer.toString()));
                this.mDocument = documentBuilder.parse(inputSource);

                return true;
            } else {
                if (connection.getResponseMessage() != null) {
                    Log.e(LOG_TAG, connection.getResponseMessage());
                } else {
                    Log.e(LOG_TAG, "Log in refused...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            } // Else do nothing
        }

        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        this.mActivity.mAuthTask = null;
        this.mActivity.showProgress(false);

        if (success) {
            this.mActivity.readXmlDoc(this.mDocument);
        } else {
            this.mActivity.mPasswordView.setError(this.mActivity.getString(R.string.error_incorrect_password));
            this.mActivity.mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        this.mActivity.mAuthTask = null;
        this.mActivity.showProgress(false);
    }
}
