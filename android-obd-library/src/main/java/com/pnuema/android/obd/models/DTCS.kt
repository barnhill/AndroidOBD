package com.pnuema.android.obd.models

import com.squareup.moshi.JsonClass

/**
 * Holder for all DTCs
 *
 * @author Brad Barnhill
 */
@JsonClass(generateAdapter = true)
class DTCS {
    var dtcs: List<DTC> = ArrayList()
}
