package jirareporter

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured([Roles.ADMIN, Roles.MANAGER, Roles.JIRA_USER])
class GitHubController {

    def filterService
    def gitHubService

    def report() {
        Date activeSince = filterService.formatActiveSince(params)
        List<Company> companies = filterService.formatCompanies(params)
        List<Product> products = filterService.formatProducts(params)
        def data = gitHubService.getHeatMap(companies, products, activeSince)
        [heatMap: data.heatMap, languages: data.languages, languagesMax: data.languagesMax]
    }
}
