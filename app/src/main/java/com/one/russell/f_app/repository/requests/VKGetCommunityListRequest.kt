package com.one.russell.f_app.repository.requests

import com.one.russell.f_app.repository.entities.CommunityResponse
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKGetCommunityListRequest(startPosition: Int, itemsCount: Int) :
    VKRequest<List<CommunityResponse>>("groups.get") {

    init {
        addParam("extended", 1)
        addParam("fields", "description,members_count")
        addParam("offset", startPosition)
        addParam("count", itemsCount)
    }

    override fun parse(r: JSONObject): List<CommunityResponse> {
        val response = r.getJSONObject("response")
        val communityList = response.getJSONArray("items")
        val result = ArrayList<CommunityResponse>()
        for (i in 0 until communityList.length()) {
            result.add(CommunityResponse.parse(communityList.getJSONObject(i)))
        }
        return result
    }
}