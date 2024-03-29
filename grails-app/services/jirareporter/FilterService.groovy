package jirareporter

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

import java.sql.Time
import java.text.SimpleDateFormat

@Transactional
class FilterService {

    def springSecurityService

    List<JiraUser> formatUsersList(params) {
        def list = (params.user?.split(',')?.collect {
            it.split('\\(')?.first()?.replace(')', '')?.trim()
        }?.findAll { it } ?: []) + [0]
        def users = JiraUser.findAllByDisplayNameInList(list)
        if (list.contains('Current User')) {
            users << JiraUser.findByName(springSecurityService.currentUser.username)
        }
        users
    }

    List<Issue> formatIssueList(params) {
        Issue.findAllByKeyInList((params.issue?.split(',')?.collect {
            it.split(':')?.first()?.trim()
        }?.findAll { it } ?: []) + [0])
    }

    List<JiraUser> formatTeamMembers(Set<String> users) {
        JiraUser.findAllByDisplayNameInList((users?.toList() ?: []) + [0])
    }

    List<String> formatWorklogTypes(params) {
        params.worklogTypes?.split(',')?.collect { it.split('\\(')?.last()?.replace(')', '')?.trim() }?.findAll {
            it
        } ?: []
    }

    List<Project> formatProjects(params) {
        Project.findAllByKeyInList((params.project?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<IssueType> formatIssueTypes(params) {
        IssueType.findAllByNameInList((params.issueType?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<Priority> formatPriorities(params) {
        Priority.findAllByNameInList((params.priority?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<Component> formatComponents(params) {
        Component.findAllByNameInList((params.component?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<Client> formatClients(params) {
        Client.findAllByNameInList((params.client?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<Label> formatLabels(params) {
        Label.findAllByNameInList((params.labels?.split(',')?.collect { it.toString()?.trim() }?.findAll {
            it
        } ?: []) + [0])
    }

    List<Status> formatStatus(params) {
        def list = []
        params.status?.split(',')?.collect { it.toString()?.trim() }?.findAll { it }?.each { st ->
            list.addAll(Configuration.statusList.find { it.name == st }?.details)
        }.findAll { it }
        Status.findAllByNameInList(list + [0])
    }

    List<Team> formatTeams(params) {
        if (params.team && params.team?.trim() != '')
            Team.findAllByIdInList((params.team?.split(',')?.collect { it.toString()?.toLong() }?.findAll {
                it
            } ?: []) + [null])
        else
            []
    }

    List<Company> formatCompanies(params) {
        if (params.company && params.company?.trim() != '')
            Company.findAllByIdInList((params.company?.split(',')?.collect { it.toString()?.toLong() }?.findAll {
                it
            } ?: []) + [null])
        else
            []
    }

    List<Product> formatProducts(params) {
        if (params.product && params.product?.trim() != '')
            Product.findAllByIdInList((params.product?.split(',')?.collect { it.toString()?.toLong() }?.findAll {
                it
            } ?: []) + [null])
        else
            []
    }

    List<IssueProduct> formatIssueProducts(params) {
        if (params.issueProduct && params.issueProduct?.trim() != '')
            IssueProduct.findAllByIdInList((params.issueProduct?.split(',')?.collect { it.toString()?.toLong() }?.findAll {
                it
            } ?: []) + [null])
        else
            []
    }

    Boolean formatNoRecurring(params) {
        params.noRecurring ? true : false
    }

    Boolean formatUnassigned(params) {
        params.unassigned ? true : false
    }

    Date formatFromDate(params) {
        if (params.from == null)
            return null
        def date = formatDate(params.from)
        if (date) {
            use(TimeCategory) {
                date = date + 1.millisecond
            }
        }
        date
    }

    Date formatToDate(params) {
        if (params.to == null)
            return null
        def date = formatDate(params.to)
        if (date) {
            date = date.clearTime() + 1
            use(TimeCategory) {
                date = date - 1.millisecond
            }
        }
        date
    }

    Date formatActiveSince(params) {
        if (params.activeSince == null)
            return null
        def date = formatDate(params.activeSince)
        if (date) {
            use(TimeCategory) {
                date = date + 1.millisecond
            }
        }
        date
    }

    Date formatDate(String input) {
        def date
        if (input.contains('/'))
            date = new Date(input).clearTime()
        else
            date = formatRelativeDate(input)?.clearTime()
//        println date
        date
    }

    Date formatRelativeDate(String input) {
        input = input.trim().toLowerCase()
        if (input == 'today')
            return new Date().clearTime()

        formatRelativeWeekDay(input) ?: formatRelativeShiftDay(input)
    }

    Date formatRelativeShiftDay(String input) {
        def scale = 'days'
        if (input.endsWith('d')) {
            scale = 'weeks'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('w')) {
            scale = 'weeks'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('m')) {
            scale = 'months'
            input = input.substring(0, input.size() - 1).trim()
        } else if (input.endsWith('y')) {
            scale = 'years'
            input = input.substring(0, input.size() - 1).trim()
        }
        def multiplier = 1
        if (input.startsWith('-')) {
            multiplier = -1
            input = input.substring(1)
        }
        try {
            def amount = input.toInteger()
            def date = new Date().clearTime()
            use(TimeCategory) {
                date = date + (multiplier * amount)."${scale}"
            }
            date
        }
        catch (ignore) {
            return null
        }
    }

    Date formatRelativeWeekDay(String input) {
        def weekDay = null
        ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'].each {
            if (input.endsWith(it))
                weekDay = it
        }
        if (!weekDay)
            return null

        input = input.substring(0, input.size() - 3)

        def multiplier = 1
        if (input.startsWith('-')) {
            multiplier = -1
            input = input.substring(1)
        }
        def amount = 1
        if (input.size() > 0)
            try {
                amount = input.toInteger()
            }
            catch (ignore) {
                return null
            }

        def counter = multiplier * amount

        def formatter = new SimpleDateFormat('EEE')
        def date = new Date().clearTime()

        if (counter < 0)
            if (weekDay == formatter.format(date).toLowerCase())
                counter++
            else
                date = date - multiplier

        if (weekDay == formatter.format(date).toLowerCase() && counter > 0)
            date = date + 1

        while (counter != 0) {
            date = date + multiplier
            while (weekDay != formatter.format(date).toLowerCase())
                date = date + (multiplier)
            counter -= multiplier
        }
        date
    }
}
