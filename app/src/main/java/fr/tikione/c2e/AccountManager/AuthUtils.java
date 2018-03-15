package fr.tikione.c2e.AccountManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import fr.tikione.c2e.R;

/**
 * Created by tuxlu on 19/12/17.
 */

public class AuthUtils {

    public static final int AUTH_REQUEST_CODE = 2816;

    public static void logout(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.logout))
                .setMessage(context.getString(R.string.logout_confirm))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AuthUtils.removeAccountLogout(context);
                    }
                })
                .setNegativeButton(context.getString(R.string.no), null)
                .show();
    }

    public static void addAccount(Context context, String username, String password) {
        Account account = new Account(username, context.getString(R.string.account_type));
        AccountManager am = AccountManager.get(context);
        am.addAccountExplicitly(account, null, null);
        am.setAuthToken(account, context.getString(R.string.account_type), password);
        am.setUserData(account, "name", username);
    }

    public static void removeAccountLogout(Context context) {
        AccountManager am = AccountManager.get(context);
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            am.removeAccountExplicitly(am.getAccountsByType(context.getString(R.string.account_type))[0]);
        } else {
            am.removeAccount(am.getAccounts()[0], null, null);
        }
    }

    public static String getUsername(Context context) {
        AccountManager am = AccountManager.get(context);
        if (am.getAccountsByType(context.getString(R.string.account_type)).length == 0)
            return "";
        return am.getUserData(am.getAccountsByType(context.getString(R.string.account_type))[0], "name");
    }

    public static String getToken(Context context) {
        AccountManager am = AccountManager.get(context);
        //should'nt happen
        if (am.getAccountsByType(context.getString(R.string.account_type)).length == 0)
            return null;
        Bundle res;
        try {
            res = am.getAuthToken(am.getAccountsByType(context.getString(R.string.account_type))[0],
                    context.getString(R.string.account_type), null, false, null, null).getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (res == null)
            return null;
        Object acc = res.get(AccountManager.KEY_AUTHTOKEN);
        if (acc != null)
            return acc.toString();
        return null;
    }

    public static boolean isAPIConnected(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(context.getString(R.string.account_type));
        return (accounts.length != 0);
    }
}
