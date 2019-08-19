package jirareporter


class IssueType {

    String url
    String name
    Boolean subtask
    String icon

    static constraints = {
    }

    @Override
    String toString(){
        name
    }
}
