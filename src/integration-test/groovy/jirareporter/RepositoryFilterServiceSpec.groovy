package jirareporter

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class RepositoryFilterServiceSpec extends Specification {

    RepositoryFilterService repositoryFilterService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new RepositoryFilter(...).save(flush: true, failOnError: true)
        //new RepositoryFilter(...).save(flush: true, failOnError: true)
        //RepositoryFilter repositoryFilter = new RepositoryFilter(...).save(flush: true, failOnError: true)
        //new RepositoryFilter(...).save(flush: true, failOnError: true)
        //new RepositoryFilter(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //repositoryFilter.id
    }

    void "test get"() {
        setupData()

        expect:
        repositoryFilterService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<RepositoryFilter> repositoryFilterList = repositoryFilterService.list(max: 2, offset: 2)

        then:
        repositoryFilterList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        repositoryFilterService.count() == 5
    }

    void "test delete"() {
        Long repositoryFilterId = setupData()

        expect:
        repositoryFilterService.count() == 5

        when:
        repositoryFilterService.delete(repositoryFilterId)
        sessionFactory.currentSession.flush()

        then:
        repositoryFilterService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        RepositoryFilter repositoryFilter = new RepositoryFilter()
        repositoryFilterService.save(repositoryFilter)

        then:
        repositoryFilter.id != null
    }
}
