package com.one.russell.f_app.repository.data_sources

import android.util.Log
import androidx.paging.PositionalDataSource
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.repository.Repository
import io.reactivex.disposables.Disposable

internal class CommunityDataSource(private val repository: Repository) :
    PositionalDataSource<Community>() {

    private lateinit var disposable: Disposable

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Community>) {
        disposable = repository.loadCommunities(params.requestedStartPosition, params.requestedLoadSize)
            .subscribe({ communities ->
                callback.onResult(communities, params.requestedStartPosition)
            }, { throwable -> Log.e("loadInitial", throwable.localizedMessage ?: "ERROR") })
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Community>) {
        disposable = repository.loadCommunities(params.startPosition, params.loadSize)
            .subscribe({ communities ->
                callback.onResult(communities)
            }, { throwable -> Log.e("loadRange", throwable.localizedMessage ?: "ERROR") })
    }
}