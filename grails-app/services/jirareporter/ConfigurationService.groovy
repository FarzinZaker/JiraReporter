package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class ConfigurationService {

    String getServerURL(){
        ''
    }

    String getUsername(){
        ''
    }

    String getPassword(){
        ''
    }
}
