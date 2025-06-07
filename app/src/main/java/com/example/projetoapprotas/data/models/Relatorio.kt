package com.example.projetoapprotas.data.models

data class FiltroRelatorio(
    val motoristaId: String? = null,
    val dataInicio: String? = null,
    val dataFim: String? = null,
    val status: StatusRegistro? = null,
    val pontoColetaId: String? = null
)

data class RelatorioResponse(
    val registros: List<RegistroPonto>,
    val estatisticas: EstatisticasRelatorio,
    val totalRegistros: Int,
    val paginaAtual: Int,
    val totalPaginas: Int
)

data class EstatisticasRelatorio(
    val totalPontos: Int,
    val pontosColetados: Int,
    val pontosNaoColetados: Int,
    val pontosParciais: Int,
    val problemasAcesso: Int,
    val percentualConclusao: Double
)

data class RelatorioDetalhado(
    val motorista: Usuario,
    val periodo: PeriodoRelatorio,
    val registros: List<RegistroPonto>,
    val estatisticas: EstatisticasRelatorio,
    val observacoes: List<String>
)

data class PeriodoRelatorio(
    val dataInicio: String,
    val dataFim: String,
    val descricao: String
) 