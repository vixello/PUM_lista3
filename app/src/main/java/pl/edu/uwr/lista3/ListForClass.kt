package pl.edu.uwr.lista3

data class ListForClass(val name: String, val description: String, val tasks: String, val image: String) {
    var id: Int = 0
    constructor(id: Int, name: String, description: String,  tasks: String, image: String) : this(name, description, tasks,image) {
        this.id = id
    }
}