package apps.base.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Utils {

    public static final String API_DAY_MONTH_YEAR = "yyyy-MM-dd";
    public static final String MONTH_DAY_YEAR = "MMMM dd, yyyy";

    public static boolean isEmailValid(@NonNull CharSequence email) {
        String regExp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z0-9]{2,25})+$";
        return Pattern.compile(regExp).matcher(email).matches();
    }

    public static String getFormattedServerName(String serverName) {
        return serverName.toLowerCase().replaceAll(" ", "_");
    }


    public static Observable<Long> pingObservable(String ip, String port, Context context) {
        Observable<Long> observable = createPingObservable(ip, port);
        if (isNetworkConnected(context)) {
            observable = observable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        }
        return observable;
    }

    public static Observable<Long> createPingObservable(String ip, String port) {
        return Observable.create(emitter -> {
            long ping = 0;
            try {
                URL url = new URL("https://" + ip + ":" + port + "/");
                String hostAddress;
                hostAddress = InetAddress.getByName(url.getHost()).getHostAddress();
                long dnsResolved = System.currentTimeMillis();
                Socket socket = new Socket(hostAddress, url.getPort());
                socket.close();
                long probeFinish = System.currentTimeMillis();
                ping = (int) (probeFinish - dnsResolved);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            emitter.onNext(ping);
            emitter.setCancellable(() -> {});
        });
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
