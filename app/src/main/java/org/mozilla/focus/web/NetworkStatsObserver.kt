package org.mozilla.focus.web

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import org.mozilla.focus.session.Session
import org.mozilla.focus.utils.NetworkStatsUtils
import java.util.concurrent.TimeUnit

private const val LOGTAG = "NetworkStatsObserver"

/** An observer that will record NetworkStats for a session. */
@TargetApi(23)
class NetworkStatsObserver(private val context: Context) : Observer<MutableList<Session>> {
    private var isSessionStarted = false
    private var startSessionMillis = -1L
    private var rxBytesAtSessionStart = -1L

    override fun onChanged(sessions: MutableList<Session>?) {
        if (sessions == null) return

        if (!isSessionStarted && sessions.size > 0) {
            isSessionStarted = true
            startSessionMillis = System.currentTimeMillis()

            // There appears to be an unspoken minimum interval for NetworkStatsManager so we calculate the delta ourselves.
            rxBytesAtSessionStart = getMobileRxBytes()
            Log.d(LOGTAG, "Starting session")

        } else if (isSessionStarted && sessions.size == 0) {
            isSessionStarted = false

            val rxBytes = getMobileRxBytes() - rxBytesAtSessionStart
            val rxMB = rxBytes / 1024f / 1024f
            val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startSessionMillis)
            Log.d(LOGTAG, "End of session. Received ${rxMB} MB in ${elapsedSeconds} seconds")
        }
    }

    private fun getMobileRxBytes() = NetworkStatsUtils.getMobileRxBytes(context, Long.MIN_VALUE, Long.MAX_VALUE)
}
