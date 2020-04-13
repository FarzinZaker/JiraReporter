package jirareporter

import grails.gorm.services.Service

@Service(RepositoryFilter)
interface RepositoryFilterService {

    RepositoryFilter get(Serializable id)

    List<RepositoryFilter> list(Map args)

    Long count()

    void delete(Serializable id)

    RepositoryFilter save(RepositoryFilter repositoryFilter)

}