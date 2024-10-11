package com.nhnacademy.http.context;

// Context 가 web server 내에서 공용으로 공유 됩니다.
public class ContextHolder {

    private static final Context context = new ApplicationContext();

    private ContextHolder() {}

    public static synchronized Context getApplicationContext() {
        return context;
    }
}
