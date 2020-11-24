package com.pnuema.android.obd.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Holder for all PIDS
 *
 * @author Brad Barnhill
 */
@Serializable
data class PIDS (
    @SerialName("pids")
    val pids: List<PID> = ArrayList()
)
