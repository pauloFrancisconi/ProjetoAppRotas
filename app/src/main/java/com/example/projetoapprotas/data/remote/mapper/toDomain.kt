package com.example.projetoapprotas.data.remote.mapper

import com.example.projetoapprotas.data.remote.dto.AddressDto
import com.example.projetoapprotas.domain.model.Address

fun AddressDto.toDomain() = Address(
    street  = street.orEmpty(),
    district = district.orEmpty(),
    city    = city.orEmpty(),
    state   = state.orEmpty()
)
