package jirareporter

class Component {

    String url
    String name
    Project project

    transient String getFullName() {
        "${project?.name?.replace('Platinum-', '')} : ${name}"
    }

    static constraints = {
        project nullable: true
    }

    static mapping = {
        version false
    }

    @Override
    String toString() {
        name
    }
}
