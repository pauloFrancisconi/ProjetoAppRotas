package com.example.pontual.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.pontual.api.models.Route
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfService {
    
    suspend fun generateRoutePdf(
        context: Context,
        route: Route,
        userName: String
    ): File = withContext(Dispatchers.IO) {
        
        // Criar diretório se não existir
        val pdfDir = File(context.getExternalFilesDir(null), "pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        
        // Nome do arquivo
        val fileName = "rota_${route.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(pdfDir, fileName)
        
        // Criar o PDF
        val writer = PdfWriter(FileOutputStream(pdfFile))
        val pdf = PdfDocument(writer)
        val document = Document(pdf)
        
        try {
            // Título
            val title = Paragraph("RELATÓRIO DE ROTA")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(title)
            
            // Informações da rota
            val routeInfo = Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(20f)
            
            routeInfo.addCell(createCell("Nome da Rota:", true))
            routeInfo.addCell(createCell(route.name, false))
            
            routeInfo.addCell(createCell("Descrição:", true))
            routeInfo.addCell(createCell(route.description ?: "Sem descrição", false))
            
            routeInfo.addCell(createCell("Motorista:", true))
            routeInfo.addCell(createCell(route.driverName ?: "Não atribuído", false))
            
            routeInfo.addCell(createCell("Criado por:", true))
            routeInfo.addCell(createCell(userName, false))
            
            routeInfo.addCell(createCell("Data de Criação:", true))
            routeInfo.addCell(createCell(formatDate(route.createdAt), false))
            
            route.estimatedDuration?.let { duration ->
                routeInfo.addCell(createCell("Duração Estimada:", true))
                routeInfo.addCell(createCell("${duration} minutos", false))
            }
            
            route.totalDistance?.let { distance ->
                routeInfo.addCell(createCell("Distância Total:", true))
                routeInfo.addCell(createCell("${String.format("%.2f", distance)} km", false))
            }
            
            routeInfo.addCell(createCell("Status:", true))
            routeInfo.addCell(createCell(if (route.isActive) "Ativa" else "Inativa", false))
            
            document.add(routeInfo)
            
            // Pontos de entrega
            if (route.points.isNotEmpty()) {
                val pointsTitle = Paragraph("PONTOS DE ENTREGA")
                    .setFontSize(16f)
                    .setBold()
                    .setMarginBottom(10f)
                document.add(pointsTitle)
                
                val pointsTable = Table(UnitValue.createPercentArray(floatArrayOf(10f, 35f, 35f, 20f)))
                    .setWidth(UnitValue.createPercentValue(100f))
                
                // Cabeçalho da tabela
                pointsTable.addHeaderCell(createHeaderCell("Seq."))
                pointsTable.addHeaderCell(createHeaderCell("Nome"))
                pointsTable.addHeaderCell(createHeaderCell("Endereço"))
                pointsTable.addHeaderCell(createHeaderCell("Contato"))
                
                // Dados dos pontos
                route.points.sortedBy { it.sequence }.forEach { point ->
                    pointsTable.addCell(createCell(point.sequence.toString(), false))
                    pointsTable.addCell(createCell(point.deliveryPoint.name, false))
                    pointsTable.addCell(createCell(point.deliveryPoint.address, false))
                    pointsTable.addCell(createCell(point.deliveryPoint.contactName ?: "N/A", false))
                }
                
                document.add(pointsTable)
            }
            
            // Rodapé
            val footer = Paragraph("Relatório gerado em ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30f)
                .setFontColor(ColorConstants.GRAY)
            document.add(footer)
            
        } finally {
            document.close()
        }
        
        pdfFile
    }
    
    private fun createCell(content: String, isBold: Boolean): Cell {
        val cell = Cell().add(Paragraph(content))
        if (isBold) {
            cell.setBold()
        }
        return cell.setPadding(8f)
    }
    
    private fun createHeaderCell(content: String): Cell {
        return Cell()
            .add(Paragraph(content))
            .setBold()
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(8f)
    }
    
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun shareOrOpenPdf(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Se não conseguir abrir, tenta compartilhar
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(shareIntent, "Compartilhar PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
} 