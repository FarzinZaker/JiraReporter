package jirareporter

class Component {

    String url
    String name
    Project project

    static constraints = {
        project nullable: true
    }

    @Override
    String toString() {
        name
    }
}
