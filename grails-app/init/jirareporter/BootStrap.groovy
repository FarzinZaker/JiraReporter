package jirareporter

class BootStrap {

    def init = { servletContext ->

        //Users & Roles
        Roles.ALL.each { authority ->
            def role = Role.findByAuthority(authority)
            if (!role)
                new Role(authority: authority).save()
        }

        def admin = User.findByUsername('admin')
        if (!admin)
            admin = new User(username: 'admin', password: 'JP!@#', displayName: 'Administrator')

        admin.accountLocked = false
        admin.accountExpired = false
        admin.passwordExpired = false
        admin.enabled = true
        admin.save()

        def adminRole = Role.findByAuthority(Roles.ADMIN)

        if (!UserRole.findByUserAndRole(admin, adminRole))
            new UserRole(user: admin, role: adminRole).save()

//        JiraUser.executeUpdate('update JiraUser set team = null')
//        CrossOverLog.executeUpdate('update CrossOverLog set team = null')
//        Configuration.crossOverTeams.each { xoTeam ->
//            def team = Team.findByXoName(xoTeam.name)
//            println team
//
//            CrossOverLog.findAllByTeamName(xoTeam.name).each{
//                println it.team
//                it.team = team
//                it.save()
//            }
//            JiraUser.findAllByTeamName(xoTeam.name).each{
//                println it.team
//                it.team = team
//                it.save(flush:true)
//            }
//        }
    }
    def destroy = {
    }
}
