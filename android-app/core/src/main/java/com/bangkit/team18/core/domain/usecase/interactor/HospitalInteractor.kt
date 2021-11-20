package com.bangkit.team18.core.domain.usecase.interactor

import com.bangkit.team18.core.domain.repository.HospitalRepository
import com.bangkit.team18.core.domain.usecase.HospitalUseCase

class HospitalInteractor(private val hospitalRepository: HospitalRepository) : HospitalUseCase {

  override suspend fun getHospitals() = hospitalRepository.getHospitals()

  override suspend fun getHospitalDetail(id: String) = hospitalRepository.getHospitalDetail(id)

  override suspend fun searchHospitals(query: String) = hospitalRepository.searchHospitals(query)
}