/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.utils

import android.annotation.TargetApi
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

class NetworkStatsUtils private constructor() {

    companion object {
        @JvmStatic
        @TargetApi(23)
        fun getMobileRxBytes(context: Context, startMillis: Long, endMillis: Long): Long {
            val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            val bucket = networkStatsManager.querySummaryForUser(ConnectivityManager.TYPE_MOBILE,
                    getMobileSubscriberID(context), startMillis, endMillis)
            return bucket.rxBytes
        }

        // Requires android.permission.READ_PHONE_STATE
        private fun getMobileSubscriberID(context: Context) =
                (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).subscriberId
    }
}