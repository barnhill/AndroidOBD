package com.pnuema.android.obd.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Holder for all PIDS
 *
 * @author Brad Barnhill
 */
@JsonClass(generateAdapter = true)
class PIDS : Serializable {
    var pids: List<PID> = ArrayList()
}
