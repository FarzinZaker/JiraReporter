package jirareporter

import grails.gorm.transactions.Transactional
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import javax.servlet.http.HttpServletResponse
import java.awt.Color
import java.text.SimpleDateFormat

@Transactional
class ExportService {

    def exportTasks(List<Issue> issues, HttpServletResponse response) {
        response.setContentType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
        response.setHeader('Content-Disposition', 'AttachmentFilename="Tasks.xlsx"')

        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Tasks")

        //Header Group Font
        Font headerGroupFont = workbook.createFont()
        headerGroupFont.setBold(true)
        headerGroupFont.setItalic(false)
        headerGroupFont.setColor(new XSSFColor(Color.WHITE))
        headerGroupFont.setFontHeightInPoints(14 as short)

        //Header Font
        Font headerFont = workbook.createFont()
        headerFont.setBold(true)
        headerFont.setItalic(false)
        headerFont.setColor(new XSSFColor(Color.WHITE))
        headerFont.setFontHeightInPoints(12 as short)

        // Setting Header Group Style
        CellStyle headerGroupStyle = workbook.createCellStyle()
        headerGroupStyle.setFillForegroundColor(new XSSFColor(Color.decode("#0D47A1")))
        headerGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerGroupStyle.setFont(headerGroupFont)

        // Setting Header Style
        CellStyle headerStyle = workbook.createCellStyle()
        headerStyle.setFillForegroundColor(new XSSFColor(Color.decode("#1E88E5")))
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerStyle.setFont(headerFont)

        // Setting Main Column Style
        CellStyle mainColumnStyle = workbook.createCellStyle()
//        mainColumnStyle.setFillForegroundColor(new XSSFColor(Color.decode("#E3F2FD")))
//        mainColumnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        //Header Groups
        int rowCount = 0
        def header = sheet.createRow(rowCount++)
        int columnCount = 0

        def cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("PARENT ISSUE")
        cell.setCellStyle(headerGroupStyle)
        3.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3))

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("ISSUE")
        cell.setCellStyle(headerGroupStyle)
        5.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 9))

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("WORK LOGS")
        cell.setCellStyle(headerGroupStyle)
        2.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 12))

        //Headers
        header = sheet.createRow(rowCount++)
        columnCount = 0

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("KEY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("SUMMARY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TYPE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("STATUS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("KEY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("SUMMARY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TYPE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("STATUS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("CLIENT")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("COMPONENTS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TIME SPENT")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TIME SPENT (SECONDS)")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("DESCRIPTION")
        cell.setCellStyle(headerStyle)

        def dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        issues.each { issue ->
            Row row = sheet.createRow(rowCount++)
            columnCount = 0

            //Parent Issue
            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue?.parent?.key)
            def link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL)
            link.setAddress("${Configuration.serverURL}/browse/${issue?.key}")
            cell.setHyperlink(link)
            cell.setCellStyle(mainColumnStyle)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue?.parent?.summary)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue?.parent?.issueType?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue?.parent?.status?.name)

            //Issue
            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.key)
            link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL)
            link.setAddress("${Configuration.serverURL}/browse/${issue.key}")
            cell.setHyperlink(link)
            cell.setCellStyle(mainColumnStyle)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.summary)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.issueType?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.status?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.clients?.collect { it.name }?.join(', '))

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(issue.components?.collect { it.name }?.join(', '))

            //Work Logs

            CellStyle descriptionCellStyle = workbook.createCellStyle()
            descriptionCellStyle.setWrapText(true)

            def description = ''
            def timeSpentSeconds = 0
            issue.assignees.keySet().eachWithIndex { author, index ->
                if (index > 0)
                    description += '\n'
                description += author.displayName + '\n'
                issue.assignees[author]?.each { worklog ->
                    description += '    - ' + worklog.timeSpent + ' (' + dateFormat.format(worklog.started) + ')\n'
                    description += worklog.comment.split('\n').collect { it.trim() }.findAll { it }.collect { '      ' + it }.join('\n') + '\n'
                    timeSpentSeconds += worklog.timeSpentSeconds
                }
            }

            def timeSpent = DurationUtil.formatDuration(timeSpentSeconds)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(timeSpent)

            cell = row.createCell(columnCount++, CellType.NUMERIC)
            cell.setCellValue(timeSpentSeconds)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(description.trim())
            cell.setCellStyle(descriptionCellStyle)
        }

        for (def x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
            sheet.autoSizeColumn(x);
        }

        workbook.write(response.outputStream)
    }

    def exportWorklogs(List<Worklog> worklogs, HttpServletResponse response) {
        response.setContentType('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
        response.setHeader('Content-Disposition', 'AttachmentFilename="Worklogs.xlsx"')

        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Worklogs")

        //Header Group Font
        Font headerGroupFont = workbook.createFont()
        headerGroupFont.setBold(true)
        headerGroupFont.setItalic(false)
        headerGroupFont.setColor(new XSSFColor(Color.WHITE))
        headerGroupFont.setFontHeightInPoints(14 as short)

        //Header Font
        Font headerFont = workbook.createFont()
        headerFont.setBold(true)
        headerFont.setItalic(false)
        headerFont.setColor(new XSSFColor(Color.WHITE))
        headerFont.setFontHeightInPoints(12 as short)

        // Setting Header Group Style
        CellStyle headerGroupStyle = workbook.createCellStyle()
        headerGroupStyle.setFillForegroundColor(new XSSFColor(Color.decode("#0D47A1")))
        headerGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerGroupStyle.setFont(headerGroupFont)

        // Setting Header Style
        CellStyle headerStyle = workbook.createCellStyle()
        headerStyle.setFillForegroundColor(new XSSFColor(Color.decode("#1E88E5")))
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        headerStyle.setFont(headerFont)

        // Setting Main Column Style
        CellStyle mainColumnStyle = workbook.createCellStyle()
//        mainColumnStyle.setFillForegroundColor(new XSSFColor(Color.decode("#E3F2FD")))
//        mainColumnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        //Header Groups
        int rowCount = 0
        def header = sheet.createRow(rowCount++)
        int columnCount = 0

        def cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("PARENT ISSUE")
        cell.setCellStyle(headerGroupStyle)
        3.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3))

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("ISSUE")
        cell.setCellStyle(headerGroupStyle)
        5.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 9))

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("WORK LOG")
        cell.setCellStyle(headerGroupStyle)
        5.times { columnCount++ }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 15))

        //Headers
        header = sheet.createRow(rowCount++)
        columnCount = 0

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("KEY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("SUMMARY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TYPE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("STATUS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("KEY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("SUMMARY")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TYPE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("STATUS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("CLIENT")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("COMPONENTS")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("AUTHOR")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("DATE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TIME SPENT")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("TIME SPENT (SECONDS)")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("BILLABLE")
        cell.setCellStyle(headerStyle)

        cell = header.createCell(columnCount++, CellType.STRING)
        cell.setCellValue("DESCRIPTION")
        cell.setCellStyle(headerStyle)

        def dateFormat = new SimpleDateFormat('yyyy-MM-dd')
        worklogs.each { worklog ->
            Row row = sheet.createRow(rowCount++)
            columnCount = 0

            //Parent Issue
            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.parent?.key)
            def link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL)
            link.setAddress("${Configuration.serverURL}/browse/${worklog.task?.key}")
            cell.setHyperlink(link)
            cell.setCellStyle(mainColumnStyle)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.parent?.summary)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.parent?.issueType?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.parent?.status?.name)

            //Issue
            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.key)
            link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL)
            link.setAddress("${Configuration.serverURL}/browse/${worklog.task?.key}")
            cell.setHyperlink(link)
            cell.setCellStyle(mainColumnStyle)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.summary)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.issueType?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.status?.name)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.clients?.collect { it.name }?.join(', '))

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.task?.components?.collect { it.name }?.join(', '))

            //Work Log
            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.author?.displayName)
            cell.setCellStyle(mainColumnStyle)

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(dateFormat.format(worklog.started))

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.timeSpent)

            cell = row.createCell(columnCount++, CellType.NUMERIC)
            cell.setCellValue(worklog.timeSpentSeconds)

            cell = row.createCell(columnCount++, CellType.BOOLEAN)
            cell.setCellValue(worklog.comment?.toUpperCase()?.contains('[BILLABLE]'))

            cell = row.createCell(columnCount++, CellType.STRING)
            cell.setCellValue(worklog.comment?.replace('[BILLABLE]', '')?.replace('[billable]', '')?.trim())
        }

        for (def x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
            sheet.autoSizeColumn(x);
        }

        workbook.write(response.outputStream)
    }
}
