package jirareporter

class Product {

    Company company
    String name

    static constraints = {
    }

    @Override
    String toString() {
        "${company?.name}: ${name}"
    }
}
