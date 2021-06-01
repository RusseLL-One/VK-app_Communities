package com.one.russell.f_app.repository.requests

import com.one.russell.f_app.data.Community
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.internal.ApiCommand

class VKLeaveCommunitiesRequest(private val communityList: List<Community>) : ApiCommand<Int>() {

    override fun onExecute(manager: VKApiManager): Int {
        for (community in communityList) {
            val call = VKMethodCall.Builder()
                .method("groups.leave")
                .args("group_id", community.id)
                .version(manager.config.version)
                .build()

            manager.execute(call)
        }
        return 1
    }
}