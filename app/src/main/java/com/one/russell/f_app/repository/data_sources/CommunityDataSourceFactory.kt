package com.one.russell.f_app.repository.data_sources

import androidx.paging.DataSource
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.repository.Repository

internal class CommunityDataSourceFactory(private val repository: Repository) :
    DataSource.Factory<Int, Community>() {

    private lateinit var dataSource: DataSource<Int, Community>

    override fun create(): DataSource<Int, Community> {
        dataSource =
            CommunityDataSource(repository)
        return dataSource
    }

    fun invalidateDataSource() {
        dataSource.invalidate()
    }
}