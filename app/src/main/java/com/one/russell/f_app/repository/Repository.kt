package com.one.russell.f_app.repository

import android.content.Context
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.data.DetailedCommunity
import com.vk.api.sdk.auth.VKAccessToken
import io.reactivex.Single

interface Repository {

    fun loadCommunities(
        startPosition: Int = DEFAULT_START_POSITION,
        itemsCount: Int = DEFAULT_COUNT
    ): Single<List<Community>>

    fun loadCommunityDetails(communityId: Int): Single<DetailedCommunity>

    fun leaveCommunities(communityList: List<Community>): Single<Int>

    fun saveAccessToken(context: Context, accessToken: VKAccessToken)

    fun getAccessToken(context: Context): VKAccessToken?
}