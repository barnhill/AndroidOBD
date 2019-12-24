package com.pnuema.android.obd.models

import java.io.Serializable

/**
 * Holder for all PIDS
 *
 * @author Brad Barnhill
 */
class PIDS : Serializable {
    var pids: List<PID> = ArrayList()
}
