package com.example.projetoapprotas.data.repository

import com.example.projetoapprotas.domain.model.Address
import com.example.projetoapprotas.domain.model.Point

interface PointRepository {
    suspend fun fetchAddress(cep: String): Result<Address>
    suspend fun save(point: Point): Result<Unit>
}
