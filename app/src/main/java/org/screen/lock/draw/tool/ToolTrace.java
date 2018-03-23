package org.screen.lock.draw.tool;

import android.app.Activity;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

public class ToolTrace {

    private ToolTrace() {}

    public static Trace startTracePerformance(Activity activity, String text) {
        Trace ret = null;
        if (ToolPermission.checkPermissionREAD_PHONE_STATE(activity)) {
            ret = FirebasePerformance.getInstance().newTrace(text);
            ret.start();
        }
        return ret;
    }

    public static void stopTracePerformance(Trace trace) {
        if (trace != null) {
            trace.stop();
        }
    }
}
