package com.automatic.net;

import retrofit.Callback;

/**
 * Class used solely to maintain protected access to these two methids inside AutomaticClientPublic
 * Created by duncancarroll on 5/17/15.
 */
public class NetworkHandler {

    AutomaticClientPublic client;

    public NetworkHandler(AutomaticClientPublic client) {
        this.client = client;
    }


    public void getTokenApi(String code, Callback<ResponsesPublic.OAuthResponse> callback) {
        client.getTokenApi(code, callback);
    }

    public ResponsesPublic.OAuthResponse refreshToken(String refreshToken) {
        return client.refreshTokenSync(refreshToken);
    }
}
