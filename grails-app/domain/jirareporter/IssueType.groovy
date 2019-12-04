package jirareporter


class IssueType {

    String url
    String name
    Boolean subtask
    String icon

    static constraints = {
    }

    static mapping = {
        version false
    }

    @Override
    String toString(){
        name
    }
}
