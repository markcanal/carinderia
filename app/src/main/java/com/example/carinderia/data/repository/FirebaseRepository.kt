package com.example.carinderia.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val network: NetworkRepository,
)