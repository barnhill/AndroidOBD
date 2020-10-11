package com.pnuema.android.obd.models

/**
 * Holder for all PIDS
 *
 * @author Brad Barnhill
 */
@kotlinx.serialization.Serializable
data class PIDS (
    var pids: List<PID> = ArrayList()
)
