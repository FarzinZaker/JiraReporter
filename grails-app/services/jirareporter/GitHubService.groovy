package jirareporter

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray

import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional
class GitHubService {

    Integer downloadRepositories(Long page) {
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        JSONArray list = JSON.parse(new URL("https://api.github.com/orgs/${Configuration.getGitHubOrganization()}/repos?access_token=${Configuration.getGitHubAccessToken()}&page=${page}").text) as JSONArray
        list.each { def repositoryData ->
            Repository repository = Repository.findByName(repositoryData.name)
            if (!repository) {
                repository = new Repository(
                        name: repositoryData.name,
                        fullName: repositoryData.full_name,
                        htmlUrl: repositoryData.html_url,
                        url: repositoryData.url,
                )
            }
            if (repositoryData.created_at)
                repository.created = dateFormat.parse(repositoryData.created_at)
            if (repositoryData.updated_at)
                repository.updated = dateFormat.parse(repositoryData.updated_at)
            if (repositoryData.pushed_at)
                repository.pushed = dateFormat.parse(repositoryData.pushed_at)
            repository.save()
            categorizeRepository(repository)
            downloadRepositoryLanguages(repository)
        }
        list.size()
    }

    def downloadRepositoryLanguages(Repository repository) {
        def list = JSON.parse(new URL("${repository.url}/languages?access_token=${Configuration.getGitHubAccessToken()}").text)
        list.each { def languageData ->
            def language = CodingLanguage.findByName(languageData.key)
            if (!language)
                language = new CodingLanguage(name: languageData.key).save()

            def repositoryLanguage = RepositoryCodingLanguage.findByRepositoryAndLanguage(repository, language)
            if (!repositoryLanguage)
                repositoryLanguage = new RepositoryCodingLanguage(repository: repository, language: language)
            repositoryLanguage.linesOfCode = languageData.value
            repositoryLanguage.save()
        }
    }

    def categorizeRepository(Repository repository) {
        def filters = RepositoryFilter.list()

        filters.sort { it.expression.size() }.each { filter ->
            Pattern pattern = Pattern.compile(filter.expression)
            Matcher m = pattern.matcher(repository.name)
            if (m.find()) {
                repository.product = filter.product
            }
        }

        repository.save()
    }

    def categorizeRepositories(Long startId, Integer pageSize) {
        def filters = RepositoryFilter.list()
        def repositories = Repository.createCriteria().list {
            gt('id', startId)
            order('id', 'asc')
            maxResults(pageSize)
        }

        filters.sort { it.expression.size() }.each { filter ->
            Pattern pattern = Pattern.compile(filter.expression)
            repositories.each { Repository repository ->
                Matcher m = pattern.matcher(repository.name)
                if (m.find()) {
                    repository.product = filter.product
                }
            }
        }

        repositories.each { it.save() }

        repositories.collect { it.id }.max()
    }

    def getHeatMap(List<Company> companies, List<Product> products, Date activeSince) {
        def result = [:]
        def languages = [:]
        def languagesMax = [:]
        def repositories = Repository.createCriteria().list {
            isNotNull('product')
            if (companies?.size()) {
                product {
                    'in'('company', companies)
                }
            }
            if (products?.size()) {
                'in'('product', products)
            }
            if (activeSince) {
                or {
                    gte('created', activeSince)
                    gte('updated', activeSince)
                    gte('pushed', activeSince)
                }
            }
        }
        repositories.each { repository ->
            def company = repository.product?.company?.name
            def product = repository.product?.name
            if (!result.containsKey(company))
                result.put(company, [:])
            if (!result[company].containsKey(product))
                result[company].put(product, [:])
            RepositoryCodingLanguage.findAllByRepository(repository).each { repositoryLanguage ->
                if (!result[company][product].containsKey(repositoryLanguage.language?.name))
                    result[company][product].put(repositoryLanguage.language?.name, repositoryLanguage.linesOfCode)
                else
                    result[company][product][repositoryLanguage.language?.name] += repositoryLanguage.linesOfCode
                if (!languages.containsKey(repositoryLanguage.language.name))
                    languages.put(repositoryLanguage.language.name, [loc: repositoryLanguage.linesOfCode, count: 1])
                else {
                    languages[repositoryLanguage.language.name]['loc'] += repositoryLanguage.linesOfCode
                    languages[repositoryLanguage.language.name]['count']++
                }

                if (!languagesMax.containsKey(repositoryLanguage.language.name))
                    languagesMax.put(repositoryLanguage.language.name, repositoryLanguage.linesOfCode)
                else if (languagesMax[repositoryLanguage.language.name] < repositoryLanguage.linesOfCode)
                    languagesMax[repositoryLanguage.language.name] = repositoryLanguage.linesOfCode
            }
        }
        result.keySet().each { company ->
            result[company].keySet().each { product ->
                languages.keySet().each { language ->
                    if (!result[company][product].containsKey(language))
                        result[company][product].put(language, 1)
                }
            }
        }
        [languages: languages, languagesMax: languagesMax, heatMap: result]
    }
}
