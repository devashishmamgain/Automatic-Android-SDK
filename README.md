# Automatic Android SDK

---

**Note: This SDK is in Alpha. Please file any bugs in the [Github Issues](https://github.com/Automatic/automatic-android-sdk/issues) tracker**

---

The Automatic SDK is the best way to build Android apps powered by [Automatic](http://automatic.com).

With the Automatic Android SDK, your users can log in to your app with their [Automatic](http://automatic.com) accounts. Think _Facebook_ or _Twitter_ login—but rather than bringing a users' social graph, instead unlocking a wealth of automotive data that you can use to supercharge your app.

<img src='https://github.com/automatic/automatic-android-sdk/blob/master/README/login_button_example.png?raw=true' alt='Log in with Automatic' height='102' width='337'/>
> Pictured: your app's new login screen

Once a user approves your app's request to access their data, your app could:

- Access your users' trips to analyze driving habits
- Query your users' cars to provide up-to-date resale values estimates
- Populate your users' profiles without a lengthy signup form
- Coming soon: Create a Service Binding to Automatic's core app to receive events such as Ignition On, Ignition Off, etc.
- :sparkles: _so much more_ :sparkles:

We can't wait to see what you build. Let's get to it!

## Usage

### 1. Register your app with Automatic

Register your app on the [Automatic Developer site][developers].

### 2. Integrating the Automatic Android SDK

1. Add the following line to your build.gradle, within your `dependencies {}` block:
	```gradle
	compile 'com.automatic.android:sdk:1.0'
	```

2. Add your client id to your AndroidManifest.xml, inside your `<application>` tag.  Your client id can be found within the Automatic [Developer Apps Manager][https://developer.automatic.com/dashboard]:
	```xml
	<meta-data android:name="com.automatic.sdk.client_id" android:value="your_client_id" />	
	```

3. Add the LoginActivity to your AndroidManifest.xml, inside your `<application>` tag:
	```xml
	<activity
        android:name="com.automatic.android.sdk.LoginActivity"
        android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
        android:theme="@android:style/Theme.Translucent.NoTitleBar" />
	```

4. Add `android.permission.INTERNET` within your `<manifest>` tag if your app does not already have this permission.
	```xml
	<uses-permission android:name="android.permission.INTERNET" />
	```

### 3. Usage

1.  Add the login button to your layout XML:
	```xml
	<com.automatic.android.sdk.LoginButton
    android:id="@+id/automatic_login"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
	``` 

2. Initialize the SDK either within an Activity context, and pass it your desired Scope as well as a reference to your button:
	```java
	Automatic.initialize(
        new Automatic.Builder(this)
            .addScopes(scopes)
            .logLevel(RestAdapter.LogLevel.FULL)
            .useLoginButton(mLoginButton, this));
	```

3. Handle authentication results.  Here you have two options:

	1. Use `loginButton.setOnLoginResultCallback()` to receive notice of authentication success or failure:
		```java
		mLoginButton.setOnLoginResultCallback(new AutomaticLoginCallbacks() {
            @Override
            public void onLoginSuccess() {
                // success!
            }

            @Override
            public void onLoginFailure(RetrofitError retrofitError) {
                // failure
            }
        });
		```

	2. Use OnActivityResult and wait for `Automatic.REQUEST_CODE`:
		```java
	    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode == Automatic.REQUEST_CODE) {
	            if (resultCode == Activity.RESULT_OK) {
	                // success!
	            }else {
	            	// failure
	            }
	        }
	    }
	    ```

4. Once the user has authenticated, use `Automatic.restApi()` to make calls against the REST API, for example:
	```java
	Automatic.restApi().getTrips(new Callback<List<ResponsesPublic.Trip>>() {
        @Override
        public void success(List<ResponsesPublic.Trip> trips, Response response) {
            // you got some trips!
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            // handle failure
        }
    });

	```

	**For more details, see the [Developer Documentation][api-docs] and the [SDK Sample App][sample-app]**

5. Coming soon: Bind to the Automatic Service class and receive instantaneous push events coming from the car, e.g.:
	- Ignition On / Off
	- MIL status
	- Trip complete
	- Hard events: Accel / Brake / Speeding

[developers]: https://developer.automatic.com
[api-docs]: https://developer.automatic.com/documentation/
[sample-app]: https://github.com/Automatic/automatic-android-sdk/tree/master/samples/AutomaticSDKSampleApp
