package jirareporter

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'worklog', action: 'report')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
