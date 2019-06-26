package jirareporter

import grails.gorm.transactions.Transactional

@Transactional
class ConfigurationService {

    String getServerURL() {
        ''
    }

    String getUsername() {
        ''
    }

    String getPassword() {
        ''
    }

    List<Map> getProjects() {
        [
                [name: 'Platinum-TAKE', key: 'PLTAKE'],
                [name: 'Platinum-Beckon', key: 'PLBECK'],
                [name: 'Platinum-NorthPlains', key: 'PLNP'],
                [name: 'Platinum-SMS', key: 'PLSMS']
        ]
    }

    List<String> getIssueTypes() {
        [
                'Bugfix', 
                'Defect', 
                'Development', 
                'Documentation', 
                'Pairing', 
                'R&D', 
                'Story', 
                'Task', 
                'Test', 
                'Bugfix Sub-Task', 
                'Development Sub-Task', 
                'Documentation Sub-Task', 
                'Pairing Sub-Task', 
                'R&D Sub-Task', 
                'Sub - task', 
                'Test Sub-Task'
        ]
    }
}
