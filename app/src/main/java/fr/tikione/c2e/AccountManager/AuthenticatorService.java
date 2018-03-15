package fr.tikione.c2e.AccountManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by tuxlu on 30/09/17.
 */

public class AuthenticatorService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            fr.tikione.c2e.AccountManager.Authenticator authenticator = new Authenticator(this);
            return authenticator.getIBinder();
        }
    }