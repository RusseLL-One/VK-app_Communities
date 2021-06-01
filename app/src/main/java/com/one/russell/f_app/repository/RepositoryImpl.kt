package com.one.russell.f_app.repository

import android.content.Context
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.data.DetailedCommunity
import com.one.russell.f_app.repository.converters.CommunityConverter
import com.one.russell.f_app.repository.entities.CommunityResponse
import com.one.russell.f_app.repository.requests.VKGetCommunityDetailsRequest
import com.one.russell.f_app.repository.requests.VKGetCommunityListRequest
import com.one.russell.f_app.repository.requests.VKLeaveCommunitiesRequest
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.exceptions.VKApiExecutionException
import io.reactivex.Single

const val DEFAULT_START_POSITION = 0
const val DEFAULT_COUNT = 1000

class RepositoryImpl : Repository {

    override fun loadCommunities(
        startPosition: Int,
        itemsCount: Int
    ): Single<List<Community>> {
        return Single.create { emitter ->
            VK.execute(
                VKGetCommunityListRequest(
                    startPosition,
                    itemsCount
                ),
                object : VKApiCallback<List<CommunityResponse>> {
                    override fun success(result: List<CommunityResponse>) {
                        val mappedResult =
                            result.map {
                                CommunityConverter.fromApiToUI(it)
                            }.asReversed()
                        emitter.onSuccess(mappedResult)
                    }

                    override fun fail(error: VKApiExecutionException) {
                        emitter.onError(error)
                    }
                })
        }
    }

    override fun loadCommunityDetails(communityId: Int): Single<DetailedCommunity> {
        return Single.create { emitter ->
            VK.execute(
                VKGetCommunityDetailsRequest(communityId),
                object : VKApiCallback<DetailedCommunity> {
                    override fun success(result: DetailedCommunity) {
                        emitter.onSuccess(result)
                    }

                    override fun fail(error: VKApiExecutionException) {
                        emitter.onError(error)
                    }
                })
        }
    }

    override fun leaveCommunities(communityList: List<Community>): Single<Int> {
        return Single.create { emitter ->
            VK.execute(
                VKLeaveCommunitiesRequest(
                    communityList
                ), object : VKApiCallback<Int> {
                override fun success(result: Int) {
                    emitter.onSuccess(result)
                }

                override fun fail(error: VKApiExecutionException) {
                    emitter.onError(error)
                }
            })
        }
    }

    override fun saveAccessToken(context: Context, accessToken: VKAccessToken) {
        val sharedPreferences = context.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        accessToken.save(sharedPreferences)
    }

    override fun getAccessToken(context: Context): VKAccessToken? {
        val sharedPreferences = context.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
        return VKAccessToken.restore(sharedPreferences)
    }
}