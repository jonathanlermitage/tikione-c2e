package fr.tikione.c2e

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kodein.instance
import fr.tikione.c2e.AccountManager.AuthUtils
import fr.tikione.c2e.core.kodein
import fr.tikione.c2e.core.model.web.Auth
import fr.tikione.c2e.core.service.web.CPCAuthService
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Created by tuxlu on 20/03/18.
 */
class Login: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

        public fun onLoginClick( v: View)
    {
        if (LoginIDInput.text.isEmpty() || LoginPasswordInput.text.isEmpty())
            return
        LoginConnectButton.text = getText(R.string.login_connect_waiting)

        val username = LoginIDInput.text.toString()
        LoginAsyncTask(this).execute(LoginIDInput.text.toString(), LoginPasswordInput.text.toString())
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private class LoginAsyncTask(nContext: Login) : AsyncTask<String, Int, Int>() {
        private val SUCCESS = 0;
        private val ERROR = 1;
        private val context: WeakReference<Login> = WeakReference(nContext)

        override fun doInBackground(vararg params: String?): Int {
            val cpcAuthService: CPCAuthService = kodein.instance()
            val auth: Auth
            try {
                auth = cpcAuthService.authenticate(params[0]!!, params[1]!!)
            } catch (e: IOException) {
                e.message.toString()
                return ERROR
            }
            var cookie = "";
            auth.cookies!!.forEach { k, v -> cookie += "$k=$v" }
            AuthUtils.addAccount(context.get(), params[0], cookie)
            return SUCCESS
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            context.get()?.LoginConnectButton?.text = context.get()?.getText(R.string.login_connect)
            if (result == SUCCESS)
                context.get()?.finish();
            //else
                //showToast
        }
    }
}