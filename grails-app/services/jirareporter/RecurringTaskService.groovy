package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

@Transactional
class RecurringTaskService {

    def issueUploadService

    def dateFormat = new SimpleDateFormat('MMM dd')

    def execute(RecurringTaskSetting setting) {
        if (!setting.enabled)
            return

        def parentTaskKey = createParentTask(setting)
        createChildTask(setting, parentTaskKey)
    }

    private String createParentTask(RecurringTaskSetting setting) {
        def issue = new Issue()
        issue.project = setting.project
        issue.assignee = setting.user
        issue.priority = Priority.findByName('Medium')

        def now = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setFirstDayOfWeek(Calendar.MONDAY)
//        cal.setTime(now)
        def month = new SimpleDateFormat("MMMM").format(cal.getTime())
        def monthNumber = new SimpleDateFormat("MM").format(cal.getTime()).toInteger()
        def year = new SimpleDateFormat("yyyy").format(cal.getTime()).toInteger()

        def recurringTask = RecurringTask.findBySettingAndYearAndMonthAndWeekIsNull(setting, year, monthNumber)
        if (recurringTask)
            return recurringTask.key

        //find startDate
        def newMonth = month
        def startDate = now.clearTime()
        while (newMonth == month) {
            use(TimeCategory) {
                startDate = startDate - 1.day
            }
            newMonth = new SimpleDateFormat("MMMM").format(startDate)
        }
        use(TimeCategory) {
            startDate = startDate + 1.day
        }
//        issue.startDate = startDate

        //find dueDate
        newMonth = month
        def dueDate = now.clearTime()
        while (newMonth == month) {
            use(TimeCategory) {
                dueDate = dueDate + 1.day
            }
            newMonth = new SimpleDateFormat("MMMM").format(dueDate)
        }
        issue.dueDate = dueDate

        def realDueDate = dueDate
        use(TimeCategory) {
            realDueDate = realDueDate - 1.day
        }

        def numberOfDays = 0
        def dayCursor = startDate
        while (dayCursor < dueDate) {
            cal.setTime(dayCursor)
            if (![Calendar.SATURDAY, Calendar.SUNDAY].contains(cal.get(Calendar.DAY_OF_WEEK)))
                numberOfDays++
            use(TimeCategory) {
                dayCursor = dayCursor + 1.day
            }
        }
        def totalEstimate = DurationUtil.formatDuration(DurationUtil.getDurationSeconds(setting.originalEstimate ?: '1h') * numberOfDays)
        issue.originalEstimate = totalEstimate
        issue.remainingEstimate = totalEstimate

        def title = "Meetings, Emails, and Jira Updates ($month $year)"
        issue.summary = title

        issue.issueType = IssueType.findByName('Task')
        def key = issueUploadService.create(issue, Client.findByName('Internal'), setting.components, ['Recurring-Task'])
        new RecurringTask(setting: setting, key: key, year: year, month: monthNumber, week: null).save(flush: true)

        key
    }

    private String createChildTask(RecurringTaskSetting setting, String parentKey) {

        def issue = new Issue()
        issue.project = setting.project
        issue.assignee = setting.user
        issue.priority = Priority.findByName('Medium')

        def now = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setFirstDayOfWeek(Calendar.MONDAY)
        cal.setTime(now)
        def month = new SimpleDateFormat("MMMM").format(cal.getTime())
        def monthNumber = new SimpleDateFormat("MM").format(cal.getTime()).toInteger()
        def year = new SimpleDateFormat("yyyy").format(cal.getTime()).toInteger()
        def week = cal.get(Calendar.WEEK_OF_MONTH)

        def recurringTask = RecurringTask.findBySettingAndYearAndMonthAndWeek(setting, year, monthNumber, week)
        if (recurringTask)
            return recurringTask.key

        //find startDate
        def newWeek = week
        def startDate = now.clearTime()
        while (newWeek == week) {
            use(TimeCategory) {
                startDate = startDate - 1.day
            }
            cal.setTime(startDate)
            newWeek = cal.get(Calendar.WEEK_OF_MONTH)
        }
        use(TimeCategory) {
            startDate = startDate + 1.day
        }
//        issue.startDate = startDate

        //find dueDate
        newWeek = week
        def dueDate = now.clearTime()
        while (newWeek == week) {
            use(TimeCategory) {
                dueDate = dueDate + 1.day
            }
            cal.setTime(dueDate)
            newWeek = cal.get(Calendar.WEEK_OF_MONTH)
        }
        issue.dueDate = dueDate

        def realDueDate = dueDate
        use(TimeCategory) {
            realDueDate = realDueDate - 1.day
        }

        def numberOfDays = 0
        def dayCursor = startDate
        while (dayCursor < dueDate) {
            cal.setTime(dayCursor)
            if (![Calendar.SATURDAY, Calendar.SUNDAY].contains(cal.get(Calendar.DAY_OF_WEEK)))
                numberOfDays++
            use(TimeCategory) {
                dayCursor = dayCursor + 1.day
            }
        }
        def totalEstimate = DurationUtil.formatDuration(DurationUtil.getDurationSeconds(setting.originalEstimate ?: '1h') * numberOfDays)
        issue.originalEstimate = totalEstimate
        issue.remainingEstimate = totalEstimate

        def title = "$month $year, Week ${week}: (${dateFormat.format(startDate)} - ${dateFormat.format(dueDate)})"
        issue.summary = title

        issue.issueType = IssueType.findByName('Sub-task')
        def key = issueUploadService.create(issue, Client.findByName('Internal'), setting.components, ['Recurring-Task'], parentKey)
        new RecurringTask(setting: setting, key: key, year: year, month: monthNumber, week: week).save(flush: true)

        key
    }
}
