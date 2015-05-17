package com.automatic;

import android.util.Log;

import com.automatic.net.OAuthHandler;
import com.automatic.net.ResponsesPublic;

/**
 * Created by duncancarroll on 5/17/15.
 */
public class OAuthHandlerSDK implements OAuthHandler {

    @Override
    public ResponsesPublic.OAuthResponse getToken() {
//            if (SET_BAD_TOKEN != null) {
//                ResponsesPublic.OAuthResponse token = Internal.getToken();
//                token.access_token = SET_BAD_TOKEN;
//                token.expires_in = 0;
//                SET_BAD_TOKEN = null; // reset flag
//                return token;
//            }
        return Internal.getToken();
    }

    @Override
    public void setToken(ResponsesPublic.OAuthResponse token) {
        Internal.get().setToken(token);
    }

    @Override
    public boolean refreshToken() {
        ResponsesPublic.OAuthResponse newToken = Automatic.sClient.refreshTokenSync(Internal.getToken().refresh_token);
        if (newToken != null) {
            Internal.get().setToken(newToken);
            return true;
        }
        return false;
    }

    @Override
    public String getClientId() {
        return Internal.get().getClientId();
    }

    @Override
    public void onRefreshFailed() {
        Log.e("Automatic SDK", "Fatal error: Couldn't refresh token!  Logging out the user rather than failing all subsequent network requests");
        Automatic.logout();
    }
}
