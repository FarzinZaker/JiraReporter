package jirareporter

class GanttColumn {

    String name
    Integer displayOrder
    Integer width
    Boolean visible

    User user

    static constraints = {
        name(unique: ['user'])
    }
}
