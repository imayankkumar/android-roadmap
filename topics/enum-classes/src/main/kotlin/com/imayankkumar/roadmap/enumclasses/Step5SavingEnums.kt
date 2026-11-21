package com.imayankkumar.roadmap.enumclasses

// Post section: "Why is saving ordinal or name risky?"
// https://imayankkumar.com/blog/kotlin-enum-class/#why-is-saving-ordinal-or-name-risky
//
// Prefs stands in for SharedPreferences. StatusV2 is the next release of the app:
// CONFIRMED is added after PLACED, and PREPARING is renamed to COOKING.
// Each version keeps its "preparing" code.

/** Stands in for SharedPreferences: values survive an app update. */
class Prefs {
    private val saved = mutableMapOf<String, Any>()
    fun putInt(key: String, value: Int) { saved[key] = value }
    fun putString(key: String, value: String) { saved[key] = value }
    fun getInt(key: String, default: Int) = saved[key] as? Int ?: default
    fun getString(key: String, default: String?) = saved[key] as? String ?: default
}

/** The next release: one constant added in the middle, one renamed. */
enum class StatusV2(val code: String) {
    PLACED("placed"),
    CONFIRMED("confirmed"),
    COOKING("preparing"),
    OUT_FOR_DELIVERY("out_for_delivery"),
    DELIVERED("delivered");

    companion object {
        fun fromCode(code: String?): StatusV2? = entries.firstOrNull { it.code == code }
    }
}

/** Almost right: save the position. After the update, it reads back a different status. */
fun saveOrdinalThenUpdate(prefs: Prefs, status: OrderStatus): StatusV2 {
    prefs.putInt("last_status", status.ordinal)
    return StatusV2.entries[prefs.getInt("last_status", 0)]
}

/** Almost right: save the name. After the rename, valueOf throws. */
fun saveNameThenUpdate(prefs: Prefs, status: OrderStatus): StatusV2 {
    prefs.putString("last_status", status.name)
    return StatusV2.valueOf(prefs.getString("last_status", null)!!)
}

/** Right: save a code you control, and handle codes you don't know. */
fun saveCodeThenUpdate(prefs: Prefs, status: OrderStatus): StatusV2 {
    prefs.putString("last_status", status.code)
    return StatusV2.fromCode(prefs.getString("last_status", null)) ?: StatusV2.PLACED
}

/** An old app reads a status the server added later. */
fun readServerStatus(code: String): OrderStatus = OrderStatus.fromCode(code) ?: OrderStatus.PLACED
