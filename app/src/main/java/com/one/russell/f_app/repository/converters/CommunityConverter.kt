package com.one.russell.f_app.repository.converters

import com.one.russell.f_app.data.Community
import com.one.russell.f_app.repository.entities.CommunityResponse

object CommunityConverter {

    fun fromApiToUI(communityResponse: CommunityResponse): Community {
        return Community(
            communityResponse.id,
            communityResponse.title,
            communityResponse.logoUrl
        )
    }
}