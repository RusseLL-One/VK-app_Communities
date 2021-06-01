package com.one.russell.f_app.data

class Community(var id: Int, var title: String, var logoUrl: String, var isSelected: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Community

        if (id != other.id) return false
        if (title != other.title) return false
        if (logoUrl != other.logoUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + logoUrl.hashCode()
        return result
    }
}