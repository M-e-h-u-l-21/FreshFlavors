package com.example.foody.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped // Because we will inject this into viewModel hence we should make it such that it retains the configuration changes
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource
) {
    val remote=remoteDataSource //Stored for later use in viewModel
}