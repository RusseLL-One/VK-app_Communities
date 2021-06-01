package com.one.russell.f_app.repository.entities

import org.json.JSONException
import org.json.JSONObject

data class CommunityDetailsResponse(var id: Int, var title: String, var screenName: String, var logoUrl: String, var description: String, var followers: Int) {

    companion object {
        fun parse(responseBody: JSONObject): CommunityDetailsResponse {
            try {
                val id = responseBody.getInt("id")
                val title = responseBody.getString("name")
                val screenName = responseBody.getString("screen_name")
                val logoUrl = responseBody.getString("photo_100")
                val description = responseBody.getString("description")
                val subscribers = responseBody.getInt("members_count")
                return CommunityDetailsResponse(
                    id,
                    title,
                    screenName,
                    logoUrl,
                    description,
                    subscribers
                )
            } catch (e: JSONException) {
                throw e
            }
        }
    }
}