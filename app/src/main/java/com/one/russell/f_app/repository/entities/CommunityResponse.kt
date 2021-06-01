package com.one.russell.f_app.repository.entities

import org.json.JSONException
import org.json.JSONObject

class CommunityResponse(var id: Int, var title: String, var logoUrl: String) {

    companion object {
        fun parse(responseBody: JSONObject): CommunityResponse {
            try {
                val id = responseBody.getInt("id")
                val title = responseBody.getString("name")
                val logoUrl = responseBody.getString("photo_100")
                return CommunityResponse(id, title, logoUrl)
            } catch (e: JSONException) {
                throw e
            }
        }
    }
}