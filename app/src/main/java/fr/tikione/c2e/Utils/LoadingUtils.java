package fr.tikione.c2e.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import fr.tikione.c2e.R;

/**
 * Created by tuxlu on 09/09/17.
 */

public class LoadingUtils {

    private static final int delay = 10000;
    private static final Handler handler = new Handler();
    private static boolean init = false;

    public static void StartLoadingView(final View view, final Context context)
    {
        if (!init) {
            Button btn = view.findViewById(R.id.noWifiButton);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            init = true;
        }
        handler.postDelayed(new Runnable(){
            public void run(){
                view.findViewById(R.id.noWifiLinear).setVisibility(View.VISIBLE);
                view.findViewById(R.id.noWifiProgressBar).setVisibility(View.INVISIBLE);
            }
        }, delay);
    }

    public static void endNoWifiView(View view)
    {
        view.findViewById(R.id.noWifiLinear).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.noWifiProgressBar).setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
    }

    public static void EndLoadingView(final View view)
    {
        handler.removeCallbacksAndMessages(null);
        endNoWifiView(view);
        view.findViewById(R.id.noWifiLayout).setVisibility(View.GONE);
    }

}

