package com.pnuema.android.obd.models

import kotlinx.serialization.Serializable

/**
 * Holder for all DTCs
 *
 * @author Brad Barnhill
 */
@Serializable
class DTCS {
    var dtcs: List<DTC> = ArrayList()
}
