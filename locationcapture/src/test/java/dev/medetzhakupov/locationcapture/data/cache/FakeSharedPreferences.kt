package dev.medetzhakupov.locationcapture.data.cache

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {
    private val data = mutableMapOf<String, Any?>()

    override fun getAll(): Map<String, *> = data

    override fun getString(key: String, defValue: String?): String? = data[key] as? String ?: defValue
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = data[key] as? Set<String> ?: defValues

    override fun getInt(key: String, defValue: Int): Int = data[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = data[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = data[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = data[key] as? Boolean ?: defValue

    override fun contains(key: String): Boolean = data.containsKey(key)

    override fun edit(): SharedPreferences.Editor = FakeSharedPreferencesEditor()

    inner class FakeSharedPreferencesEditor : SharedPreferences.Editor {
        override fun putString(key: String, value: String?): SharedPreferences.Editor = apply { data[key] = value }
        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor = apply { data[key] = values }
        override fun putInt(key: String, value: Int): SharedPreferences.Editor = apply { data[key] = value }
        override fun putLong(key: String, value: Long): SharedPreferences.Editor = apply { data[key] = value }
        override fun putFloat(key: String, value: Float): SharedPreferences.Editor = apply { data[key] = value }
        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor = apply { data[key] = value }

        override fun remove(key: String): SharedPreferences.Editor = apply { data.remove(key) }
        override fun clear(): SharedPreferences.Editor = apply { data.clear() }

        override fun commit(): Boolean = true // Always simulate a successful commit
        override fun apply() {} // No-op for apply, as changes are immediate in the in-memory map
    }

    // Unimplemented listener methods (unused in unit testing)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}
