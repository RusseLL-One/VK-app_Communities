package com.one.russell.f_app.repository.requests

import com.one.russell.f_app.data.DetailedCommunity
import com.one.russell.f_app.repository.entities.CommunityDetailsResponse
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VKGetCommunityDetailsRequest(private val communityId: Int) :
    ApiCommand<DetailedCommunity>() {

    override fun onExecute(manager: VKApiManager): DetailedCommunity {
        val communityDetailsCall = VKMethodCall.Builder()
            .method("groups.getById")
            .args("group_id", communityId)
            .args("extended", 1)
            .args("fields", "description,members_count")
            .version(manager.config.version)
            .build()

        val communityDetails = manager.execute(communityDetailsCall,
            CommunityDetailsParser()
        )

        val friendsCountCall = VKMethodCall.Builder()
            .method("groups.getMembers")
            .args("group_id", communityId)
            .args("filter", "friends")
            .version(manager.config.version)
            .build()

        val friendsCount = manager.execute(friendsCountCall,
            FriendsCountParser()
        )

        val lastPostDateCall = VKMethodCall.Builder()
            .method("wall.get")
            .args("owner_id", -communityId)
            .args("count", 2)
            .args("fields", "is_pinned")
            .version(manager.config.version)
            .build()

        val lastPostDate = manager.execute(lastPostDateCall,
            LastPostDateParser()
        )

        return DetailedCommunity(
            communityDetails.id,
            communityDetails.title,
            communityDetails.screenName,
            communityDetails.logoUrl,
            communityDetails.description,
            communityDetails.followers,
            friendsCount,
            lastPostDate
        )
    }

    private class CommunityDetailsParser : VKApiResponseParser<CommunityDetailsResponse> {
        override fun parse(responseBody: String): CommunityDetailsResponse {
            val json = JSONObject(responseBody)
            val community = json.getJSONArray("response").getJSONObject(0)
            return CommunityDetailsResponse.parse(community)
        }
    }

    private class FriendsCountParser : VKApiResponseParser<Int> {
        override fun parse(responseBody: String): Int {
            val json = JSONObject(responseBody)
            val response = json.getJSONObject("response")
            return response.getInt("count")
        }
    }

    private class LastPostDateParser : VKApiResponseParser<Date> {
        override fun parse(responseBody: String): Date {
            val json = JSONObject(responseBody)
            val response = json.getJSONObject("response")
            val wallList = response.getJSONArray("items")
            val isFirstPostPinned = checkIsPinned(wallList.getJSONObject(0))
            val wall = if (isFirstPostPinned != 1) wallList.getJSONObject(0) else wallList.getJSONObject(1)
            val dateSeconds = wall.getLong("date")
            return Date(dateSeconds * 1000L)
        }

        private fun checkIsPinned(wallPost: JSONObject): Int {
            return try {
                wallPost.getInt("is_pinned")
            } catch (e: JSONException) {
                0
            }
        }
    }
}