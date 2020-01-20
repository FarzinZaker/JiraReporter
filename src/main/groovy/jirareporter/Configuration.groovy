package jirareporter

class Configuration {

    static String getServerURL() {
        'https://jira.devfactory.com'
    }

    static String getUsername() {
        'jplanner'
    }

    static String getPassword() {
        ''
    }

    static String getCrossOverUsername() {
        'farzin.zaker@aclate.com'
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
                [name: 'Platinum-SMS', key: 'PLSMS']
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
                        'Professional Services Review'
                ]],
                [name: 'To Do', details: [
                        'Backlog',
                        'Open',
                        'Reopened',
                        'To Do',
                        'Selected For Work',
                        'Pending Eng Assistance'
                ]],
                [name: 'In Progress', details: [
                        'In Progress',
                        'Implementation',
                        'Waiting For Fix',
                        'RCA In Study',
                        'RCA Approval',
                        'Solution In Study',
                        'Pending Eng Fix'
                ]],
                [name: 'Blocked', details: [
                        'Needs Info',
                        'Paused',
                        'Further Info Needed',
                        'Waiting For Client Response'
                ]],
                [name: 'Verification', details: [
                        'In Verification',
                        'In Review',
                        'Closure Approval',
                        'Quality Assurance',
                        'QE Review'

                ]],
                [name: 'Closed', details: [
                        'Resolved',
                        'Closed',
                        'Done',
                        'Rejected',
                        'Consulting Task',
                        'Acceptance Test',
                        'Pending Customer Approval'
                ]]
        ]
    }
}
