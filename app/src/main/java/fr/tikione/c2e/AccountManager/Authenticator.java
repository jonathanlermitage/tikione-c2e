package fr.tikione.c2e.AccountManager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by tuxlu on 30/09/17.
 */

public class Authenticator extends AbstractAccountAuthenticator {

    private final Context context;

    public Authenticator(Context ncontext) {
        super(ncontext);
        context = ncontext;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        /*
        //who needs it anyway?
        intent.putExtra(context.getString(R.string.account_type) , accountType);
        intent.putExtra("full_access", authTokenType);
        //intent.putExtra("is_adding_new_account", true);
        */

        /*
        opens a login page
        final Intent intent = new Intent(context, Login.class);

        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
        */
        return new Bundle();
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        AccountManager am = AccountManager.get(context);
        String authToken = am.peekAuthToken(account, authTokenType);

        /*
        if (authToken == null || authToken.isEmpty()) {
            //méthode de reconnexion: am.getUserData(account, "refreshToken"); ...
        }
        */

        if (authToken != null && !authToken.isEmpty()) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
        return null;
        //useless, à moins de faire une connection obligatoire à l'app.
        //voir https://github.com/SamuGG/android-account-manager-example/tree/master/Account%20Manager/src/com/samugg/example
        //return addAccount(response, account.type, authTokenType, null, options);
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
