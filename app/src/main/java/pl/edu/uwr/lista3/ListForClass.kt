package pl.edu.uwr.lista3

data class ListForClass(val name: String, val description: String, val tasks: String) {
    var id: Int = 0
    constructor(id: Int, name: String, description: String,  tasks: String) : this(name, description, tasks) {
        this.id = id
    }
}