package com.example.projetoapprotas.data.repository

import com.example.projetoapprotas.data.remote.mapper.toDomain
import com.example.projetoapprotas.data.remote.service.ViaCepApi
import com.example.projetoapprotas.domain.model.Address
import com.example.projetoapprotas.domain.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PointRepositoryImpl(
    private val viaCepApi: ViaCepApi
) : PointRepository {

    override suspend fun fetchAddress(cep: String): Result<Address> = runCatching {
        withContext(Dispatchers.IO) {
            val dto = viaCepApi.getAddress(cep)
            if (dto.erro == true) throw IllegalArgumentException("CEP não encontrado")
            dto.toDomain()
        }
    }

    override suspend fun save(point: Point): Result<Unit> = runCatching {
        /* TODO: enviar para seu backend */ 
    }
}
