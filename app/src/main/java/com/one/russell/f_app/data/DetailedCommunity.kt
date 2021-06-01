package com.one.russell.f_app.data

import java.util.*

class DetailedCommunity(var id: Int, var title: String, var screenName: String, var logoUrl: String, var description: String, var followers: Int, var friends: Int, var lastPostDate: Date) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DetailedCommunity

        if (id != other.id) return false
        if (title != other.title) return false
        if (screenName != other.screenName) return false
        if (logoUrl != other.logoUrl) return false
        if (description != other.description) return false
        if (followers != other.followers) return false
        if (friends != other.friends) return false
        if (lastPostDate != other.lastPostDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + screenName.hashCode()
        result = 31 * result + logoUrl.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + followers
        result = 31 * result + friends
        result = 31 * result + lastPostDate.hashCode()
        return result
    }
}