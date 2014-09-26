
package com.example.proto.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.SystemClock;
import android.util.Log;

public class MyLog {
    public static String TAG = "MyLog";

    //해당 값을 전역으로 사용해서 해당값이 false일 경우 로그을 남기지 않는다.
    public static boolean DEBUG = true;

    public static void setTag(String tag) {
        d("Changing log tag to %s", tag);
        TAG = tag;

        DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
    }

    public static void v(String format, Object... args) {
        if (DEBUG) {
            Log.v(TAG, buildMessage(format, args));
        }
    }

    public static void d(String format, Object... args) {
    	if (DEBUG) {
    		Log.d(TAG, buildMessage(format, args));
    	}        
    }

    public static void e(String format, Object... args) {
    	if (DEBUG) {
    		Log.e(TAG, buildMessage(format, args));
    	}        
    }

    public static void e(Throwable tr, String format, Object... args) {
    	if (DEBUG) {
    		Log.e(TAG, buildMessage(format, args), tr);
    	}        
    }

    public static void wtf(String format, Object... args) {
    	if (DEBUG) {
    		Log.wtf(TAG, buildMessage(format, args));
    	}        
    }

    public static void wtf(Throwable tr, String format, Object... args) {
    	if (DEBUG) {
    		Log.wtf(TAG, buildMessage(format, args), tr);
    	}        
    }


    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.KOREA, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(MyLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.KOREA, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }
   
    //쓰레드가 생성되서 실행되는 시간을 체크하기 위한 log 클래스, 테스트 용으로 사용
    public static class MarkerLog {
        public static final boolean ENABLED = MyLog.DEBUG;

        private static final long MIN_DURATION_FOR_LOGGING_MS = 0;

        private static class Marker {
            public final String name;
            public final long thread;
            public final long time;

            public Marker(String name, long thread, long time) {
                this.name = name;
                this.thread = thread;
                this.time = time;
            }
        }

        private final List<Marker> markers = new ArrayList<Marker>();
        private boolean finished = false;

        public synchronized void add(String name, long threadId) {
            if (finished) {
                throw new IllegalStateException("Marker added to finished log");
            }

            markers.add(new Marker(name, threadId, SystemClock.elapsedRealtime()));
        }

        public synchronized void finish(String header) {
            finished = true;

            long duration = getTotalDuration();
            if (duration <= MIN_DURATION_FOR_LOGGING_MS) {
                return;
            }

            long prevTime = markers.get(0).time;
            d("(%-4d ms) %s", duration, header);
            for (Marker marker : markers) {
                long thisTime = marker.time;
                d("(+%-4d) [%2d] %s", (thisTime - prevTime), marker.thread, marker.name);
                prevTime = thisTime;
            }
        }

        @Override
        protected void finalize() throws Throwable {            
            if (!finished) {
                finish("Request on the loose");
                e("Marker log finalized without finish() - uncaught exit point for request");
            }
        }
       
        private long getTotalDuration() {
            if (markers.size() == 0) {
                return 0;
            }

            long first = markers.get(0).time;
            long last = markers.get(markers.size() - 1).time;
            return last - first;
        }
    }
}
