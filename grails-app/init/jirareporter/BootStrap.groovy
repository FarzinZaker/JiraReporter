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
            admin = new User(username: 'admin', password: 'JP!@#', firstName: 'System', lastName: 'Administrator')

        admin.accountLocked = false
        admin.accountExpired = false
        admin.passwordExpired = false
        admin.enabled = true
        admin.save()

        def adminRole = Role.findByAuthority(Roles.ADMIN)

        if (!UserRole.findByUserAndRole(admin, adminRole))
            new UserRole(user: admin, role: adminRole).save()
    }
    def destroy = {
    }
}
