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
                [name: 'Platinum-OneSpot', key: 'PLOS'],
                [name: 'Platinum-Zumobi', key: 'PLZM'],
                [name: 'Platinum-SMS', key: 'PLSMS'],
                [name: 'Platinum-Symphony', key: 'PLSYM']
        ]
    }

    static List<String> getIssueTypes() {
        IssueType.createCriteria().list {
            projections {
                property('name')
            }
        }.sort() as List<String>
    }

//    static List<Map> getCrossOverTeams() {
//        [
//                [name: 'ACQ.Consulting', team: '2997'],
//                [name: 'ACQ.MSConsulting', team: '3764', manager: '2089134'],
//                [name: 'ACQ.PlatinumEng', team: '3439', manager: '2089134']
//        ]
//    }

    static List<Map> getStatusList() {
        [
                [name: 'Backlog', details: [
                        'Backlog',
                        'Draft',
                        'Problem Registered',
                        'Professional Services Review',
                        'Waiting For Fix',
                        'Failed Fix',
                        'Pending Eng Assistance',
                        'Failed QB',
                        'Defined',
                        'In Triage'
                ]],
                [name: 'To Do', details: [
                        'Backlog',
                        'Open',
                        'Reopened',
                        'To Do',
                        'Selected For Work',
                        'Pending Eng Assistance',
                        'RCA Review',
                        'Pending Eng Fix'
                ]],
                [name: 'In Progress', details: [
                        'In Progress',
                        'Implementation',
                        'Waiting For Fix',
                        'RCA In Study',
                        'RCA Approval',
                        'Solution In Study',
                        'RCA In Progress'
                ]],
                [name: 'Blocked', details: [
                        'Needs Info',
                        'Paused',
                        'Further Info Needed',
                        'Waiting For Client Response',
                        'Blocked'
                ]],
                [name: 'Verification', details: [
                        'In Verification',
                        'In Review',
                        'Closure Approval',
                        'Quality Assurance',
                        'QE Review',
                        'RCA Approval',
                        'Request Exemption From RQB',
                        'Exempted From RQB'
                ]],
                [name: 'Closed', details: [
                        'Resolved',
                        'Closed',
                        'Done',
                        'Rejected',
                        'Consulting Task',
                        'Acceptance Test',
                        'Pending Customer Approval',
                        'Cancelled',
                        'Released',
                        'Release Pending',
                        'Archived'
                ]]
        ]
    }
}
