package jirareporter

class Configuration {

    static String getServerURL() {
        'https://jira.devfactory.com'
    }

    static String getUsername() {
        ''
    }

    static String getPassword() {
        ''
    }

    static String getCrossOverUsername() {
        ''
    }

    static String getCrossOverPassowrd() {
        ''
    }

    static List<Map> getProjects() {
        [
                [name: 'Platinum-TAKE', key: 'PLTAKE'],
                [name: 'Platinum-Beckon', key: 'PLBECK'],
                [name: 'Platinum-NorthPlains', key: 'PLNP'],
                [name: 'Platinum-SMS', key: 'PLSMS'],
                [name: 'Platinum-OneSpot', key: 'PLOS']
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
                'Sub-task',
                'Test Sub-Task'
        ]
    }
}
