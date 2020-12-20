package com.example.openweather

class Cities {
    var id = 0
    var name: String
    var country: String

    constructor(name: String, country: String) {
        this.name = name
        this.country = country
    }

    constructor(id: Int, name: String, country: String) {
        this.id = id
        this.name = name
        this.country = country
    }
}