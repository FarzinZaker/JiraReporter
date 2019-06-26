package jirareporter

class Configuration {

    static String getServerURL() {
        ''
    }

    static String getUsername() {
        ''
    }

    static String getPassword() {
        ''
    }

    static List<Map> getProjects() {
        [
                [name: 'Platinum-TAKE', key: 'PLTAKE'],
                [name: 'Platinum-Beckon', key: 'PLBECK'],
                [name: 'Platinum-NorthPlains', key: 'PLNP'],
                [name: 'Platinum-SMS', key: 'PLSMS']
        ]
    }

    static List<String> getIssueTypes() {
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
