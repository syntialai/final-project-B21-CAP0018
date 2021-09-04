package com.bangkit.team18.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val firebaseModule = module {
  single { FirebaseFirestore.getInstance() }
  single { FirebaseAuth.getInstance() }
  single { FirebaseStorage.getInstance() }
}

val dispatcherModule = module {
  single { Dispatchers.IO }
}
