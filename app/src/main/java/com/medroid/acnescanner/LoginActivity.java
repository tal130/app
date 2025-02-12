package com.medroid.acnescanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //user already signin
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            // show the signup or login screen
            setContentView(R.layout.activity_login);

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin(0);
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);

            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(0);
                }
            });

            mEmailSignUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(1);
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);


            //facebook login
            callbackManager = CallbackManager.Factory.create();

            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject me, GraphResponse response) {
                                    if (response.getError() != null) {
                                        Log.e("LoginActivity", "error");
                                        // handle error
                                    } else {
                                        //TODO need to make sure email is not empthy, user can signin to facebook with phone number
                                        String email = me.optString("email");
                                        Log.e("LoginActivity", email);

                                        try {
                                            SignupToDB(email, me.getString("id"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.i("login canceled", "");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e("error facebook", "");
                    Toast toast = Toast.makeText(getApplicationContext(), "an error occur. check your internet connection.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(int type) {
        /**
         *@param type -  0 or 1. 0 to login 1 to register
         */
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if (type == 0)
                logInToDB(email,password);
            else
                SignupToDB(email, password);
        }
    }


    private void SignupToDB(String email, String password)
    {
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // The user is logged in.

//                             TODO: for later use installation for pushup notification
//                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//                            installation.put("email", email);
//                            installation.saveInBackground();


                            Log.i("register","successful");
                            showProgress(false);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.e("register","failed");
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    switch (e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            showProgress(false);
                            mEmailView.setError("USERNAME_TAKEN");
                            mEmailView.requestFocus();
                            Log.d("Testing","Sorry, this username has already been taken.");
                            break;
                        case ParseException.USERNAME_MISSING:
                            Log.d("Testing","Sorry, you must supply a username to register.");
                            showProgress(false);
                            mEmailView.setError("USERNAME_MISSING");
                            mEmailView.requestFocus();
                            break;
                        case ParseException.PASSWORD_MISSING:
                            Log.d("Testing","Sorry, you must supply a password to register.");
                            showProgress(false);
                            mPasswordView.setError("PASSWORD_MISSING");
                            mPasswordView.requestFocus();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Log.d("Testing","Internet connection was not found. Please see your connection settings.");
                            showProgress(false);
                            mEmailView.setError("CONNECTION_FAILED");
                            mEmailView.requestFocus();
                            break;
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            Log.d("Testing","Internet connection was not found. Please see your connection settings.");
                            showProgress(false);
                            mEmailView.setError(getText(R.string.error_invalid_email));
                            mEmailView.requestFocus();
                            break;
                        default:
                            Log.d("Testing",e.getLocalizedMessage());
                            showProgress(false);
                            mEmailView.setError("User or password are wrong");
                            mEmailView.requestFocus();
                            break;
                    }
                }
            }
        });
    }


    private void logInToDB(final String email,String password)
    {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
                    Log.e("log in", "successful");
                    // The user is logged in.

//                    TODO: for later use installation for pushup notification
//                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//                    installation.put("email", email);
//                    installation.saveInBackground();

                    Log.e("installation", "successful");

                    showProgress(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    //  finish();
                } else {
                    Log.e("log in", "failed");
                    // Login failed. Look at the ParseException to see what happened.
                    switch (e.getCode()) {
                        case ParseException.PASSWORD_MISSING:
                            Log.d("Testing", "Sorry, Enter password.");
                            showProgress(false);
                            mPasswordView.setError("Enter password");
                            mPasswordView.requestFocus();
                            break;

                        case ParseException.EMAIL_NOT_FOUND:
                            Log.d("Testing", "Sorry, EMAIL_NOT_FOUND.");
                            showProgress(false);
                            mEmailView.setError("User not exist");
                            mEmailView.requestFocus();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Log.d("Testing", "Internet connection was not found. Please see your connection settings.");
                            showProgress(false);
                            mEmailView.setError("CONNECTION_FAILED");
                            mEmailView.requestFocus();
                            break;
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            Log.d("Testing", "Internet connection was not found. Please see your connection settings.");
                            showProgress(false);
                            mEmailView.setError(getText(R.string.error_invalid_email));
                            mEmailView.requestFocus();
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            Log.d("Testing", "OBJECT_NOT_FOUND");
                            showProgress(false);
                            mEmailView.setError(getText(R.string.error_incorrect_password));
                            mEmailView.requestFocus();
                            break;
                        default:
                            Log.d("Testing", "" + e.getCode());
                            showProgress(false);
                            mPasswordView.setError("User or Password not correct");
                            mPasswordView.requestFocus();
                            break;
                    }
                }
            }
        });
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
}

