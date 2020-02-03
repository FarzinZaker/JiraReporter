package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class UserController {

    def userService
    def springSecurityService

    @Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
    def search() {
        render((params.id ? userService.search(params.id)?.collect {
            [
                    value: "${it.displayName} (${it.name})"
            ]
        } : []) as JSON)
    }

    @Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
    def systemSearch() {
        render((params.id ? userService.systemSearch(params.id)?.collect {
            [
                    id  : it.id,
                    name: it.displayName
            ]
        } : []) as JSON)
    }

    @Secured([Roles.ADMIN])
    def list() {
        [role: params.id]
    }

    @Secured([Roles.ADMIN])
    def listJSON() {

        def value = [:]
        def parameters = [offset: params.skip, max: params.pageSize, sort: params["sort[0][field]"] ?: "username", order: params["sort[0][dir]"] ?: "asc"]

        def list
        def role = Role.findByAuthority(params.id)
        def idList = UserRole.findAllByRole(role)?.collect { it.userId } ?: [0]
        list = User.findAllByIdInList(idList, parameters)
        value.total = User.countByIdInList(idList)

        value.data = list.collect {
            [
                    id             : it.id,
                    displayName    : it.displayName,
                    username       : it.username?.replace('@', ' @ '),
                    enabled        : it.enabled,
                    accountExpired : it.accountExpired,
                    accountLocked  : it.accountLocked,
                    passwordExpired: it.passwordExpired
            ]
        }

        render value as JSON
    }

    @Secured([Roles.ADMIN])
    def save() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        def result = []
        models.each { model ->
            def item = new User()
            if (model.id)
                item = User.get(model.id)

            item.displayName = model.displayName
            item.username = model.username
            if (model.password)
                item.password = model.password

            item.enabled = model.containsKey('enabled') && model.enabled
            item.accountExpired = model.containsKey('accountExpired') && model.accountExpired
            item.accountLocked = model.containsKey('accountLocked') && model.accountLocked
            item.passwordExpired = model.containsKey('passwordExpired') && model.passwordExpired

//            println params

            result << item.save(flush: true)

            if (params.id) {
                def role = Role.findByAuthority(params.id)
                if (!UserRole.findByUserAndRole(item, role))
                    new UserRole(user: item, role: role).save(flush: true)
            }
        }
        render(result as JSON)
    }

    @Secured([Roles.ADMIN])
    def delete() {
        def models = JSON.parse(params.models).collect { it.toSpreadMap() }
        models.each { model ->
            def item = User.get(model.id)
            UserRole.findAllByUser(item).each { it.delete() }
            TeamManager.findAllByManager(item).each { it.delete() }
            item.delete(flush: true)
        }
        render([] as JSON)
    }

    @Secured([Roles.ADMIN])
    def roles() {
        [roles: User.get(params.id)?.authorities?.collect { it.authority }]
    }

    @Secured([Roles.ADMIN])
    def saveRoles() {
        def roles = Roles.ALL.findAll { params."${it}" }
        def user = User.get(params.id)
        def userRoles = UserRole.findAllByUser(user)
        userRoles.findAll { !roles.contains(it.role.authority) }.each { it.delete(flush: true) }
        roles.findAll { role ->
            !userRoles.any {
                it.role.authority == role
            }
        }.each { role -> new UserRole(user: user, role: Role.findByAuthority(role)).save(flush: true) }
        render 1
    }

    @Secured([Roles.ADMIN])
    def teams() {
        [teams: TeamManager.findAllByManager(User.get(params.id))?.collect { it.team }]
    }

    @Secured([Roles.ADMIN])
    def saveTeams() {
        def teams = Team.list().findAll { params."team_${it.id}" }
        def user = User.get(params.id)
        def teamManagers = TeamManager.findAllByManager(user)
        teamManagers.findAll { !teams.collect { it.id }.contains(it.team.id) }.each { it.delete(flush: true) }
        teams.findAll { team ->
            !teamManagers.any {
                it.team == team
            }
        }.each { team -> new TeamManager(manager: user, team: team).save(flush: true) }
        render 1
    }

    @Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
    def changePassword() {
        def user = User.findByUsername((springSecurityService.currentUser as User).username)
        [askForOldPassword: user.password != springSecurityService.encodePassword(' ')]
    }

    @Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
    def saveNewPassword() {
        def user = User.findByUsername((springSecurityService.currentUser as User).username)
        if (springSecurityService.passwordEncoder.isPasswordValid(user.password, params.oldPassword, null)) {
            if (params.newPassword.trim() != '') {
                if (params.newPassword == params.newPassword_confirmation) {
                    user.password = params.newPassword
                    if (user.validate() && user.save(flush: true)) {
                        flash.message = 'Your new password has been saved successfuly'
                        redirect(action: 'passwordChanged')
                    } else {
                        flash.validationError = 'We cannot save your new password now. Please try again later.'
                        redirect(action: 'changePassword')
                    }
                } else {
                    flash.validationError = 'New passwords does not match.'
                    redirect(action: 'changePassword')
                }
            } else {
                flash.validationError = 'You cannot choose an empty password.'
                redirect(action: 'changePassword')
            }
        } else {
            flash.validationError = 'You have entered your old password incorrectly.'
            redirect(action: 'changePassword')
        }
    }

    @Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
    def passwordChanged() {

    }
}
