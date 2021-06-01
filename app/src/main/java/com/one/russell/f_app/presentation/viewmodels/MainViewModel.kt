package com.one.russell.f_app.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.one.russell.f_app.repository.data_sources.CommunityDataSourceFactory
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.data.DetailedCommunity
import com.one.russell.f_app.repository.Repository
import com.one.russell.f_app.repository.RepositoryImpl
import com.vk.api.sdk.auth.VKAccessToken
import io.reactivex.disposables.CompositeDisposable

class MainViewModel : ViewModel() {
    private val repository: Repository =
        RepositoryImpl()
    private val selectedItemsList = MutableLiveData<ArrayList<Community>>(ArrayList())
    private lateinit var pagedListLiveData: LiveData<PagedList<Community>>
    private lateinit var sourceFactory: CommunityDataSourceFactory
    private var leaveButtonVisibilityLiveData = MediatorLiveData<Boolean>()

    private var compositeDisposable = CompositeDisposable()

    init {
        setupPagedList()

        leaveButtonVisibilityLiveData.addSource(
            selectedItemsList
        ) { list ->
            val isVisible = list.size > 0
            if (leaveButtonVisibilityLiveData.value != isVisible) {
                leaveButtonVisibilityLiveData.value = isVisible
            }
        }
    }

    private fun setupPagedList() {
        sourceFactory =
            CommunityDataSourceFactory(repository)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        pagedListLiveData = LivePagedListBuilder(sourceFactory, config)
            .build()
    }

    fun loadCommunityDetails(community: Community, callback: Consumer<DetailedCommunity>) {
        compositeDisposable.add(
            repository.loadCommunityDetails(community.id)
                .subscribe({ communitiesList ->
                    callback.accept(communitiesList)
                }, { throwable ->
                    Log.e("loadCommunityDetails", throwable.localizedMessage ?: "ERROR")
                })
        )
    }

    fun leaveSelectedCommunities() {
        val communitiesToDelete = selectedItemsList.value ?: return

        compositeDisposable.add(
            repository.leaveCommunities(communitiesToDelete)
                .subscribe({ resultCode ->
                    sourceFactory.invalidateDataSource()
                    selectedItemsList.value = ArrayList()
                }, { throwable ->
                    Log.e("leaveSelectedCommun", throwable.localizedMessage ?: "ERROR")
                })
        )
    }

    fun selectItem(item: Community) {
        if (item.isSelected) {
            selectedItemsList.value?.add(item)
        } else {
            selectedItemsList.value?.remove(item)
        }
        selectedItemsList.value = selectedItemsList.value // Just to notify observers
    }

    fun getSelectedItems(): LiveData<ArrayList<Community>> {
        return selectedItemsList
    }

    fun saveAccessToken(context: Context, accessToken: VKAccessToken) {
        repository.saveAccessToken(context, accessToken)
    }

    fun getAccessToken(context: Context): VKAccessToken? {
        return repository.getAccessToken(context)
    }

    fun getCommunityList(): LiveData<PagedList<Community>> {
        return pagedListLiveData
    }

    fun getleaveButtonVisibilityLiveData(): LiveData<Boolean> {
        return leaveButtonVisibilityLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}