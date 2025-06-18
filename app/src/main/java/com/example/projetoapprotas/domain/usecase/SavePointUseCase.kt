package com.example.projetoapprotas.domain.usecase

import com.example.projetoapprotas.data.repository.PointRepository
import com.example.projetoapprotas.domain.model.Point
import javax.inject.Inject

class SavePointUseCase @Inject constructor(
    private val repo: PointRepository
) {
    suspend operator fun invoke(point: Point) = repo.save(point)
}
