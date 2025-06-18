package com.example.projetoapprotas.domain.usecase

import com.example.projetoapprotas.data.repository.PointRepository
import javax.inject.Inject

class FetchAddressUseCase @Inject constructor(
    private val repo: PointRepository
) {
    suspend operator fun invoke(cep: String) = repo.fetchAddress(cep)
}
