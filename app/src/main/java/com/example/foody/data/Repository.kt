package com.example.foody.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped // Because we will inject this into viewModel hence we should make it such that it retains the configuration changes
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remote=remoteDataSource //Stored for later use in viewModel
    val local = localDataSource
}