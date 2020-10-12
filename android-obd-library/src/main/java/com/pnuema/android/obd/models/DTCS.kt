package com.pnuema.android.obd.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Holder for all DTCs
 *
 * @author Brad Barnhill
 */
@Serializable
data class DTCS (
    @SerialName("dtcs")
    val dtcs: List<DTC> = ArrayList()
)
