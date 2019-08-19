package jirareporter

class Project {

    String url
    String name
    String key
    String avatar

    static mapping = {
        key column: 'jira_key'
    }

    static constraints = {
    }

    @Override
    String toString(){
        name
    }
}
