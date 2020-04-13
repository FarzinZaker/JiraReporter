package jirareporter

class Repository {

    Product product

    String name
    String fullName
    String htmlUrl
    String url
    Date created
    Date updated
    Date pushed

    static constraints = {
        product nullable: true
        created nullable: true
        updated nullable: true
        pushed nullable: true
    }

    @Override
    String toString() {
        name
    }
}
