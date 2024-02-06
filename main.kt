import java.io.File

sealed class Command {
    object Exit : Command() {
        override fun isValid(): Boolean {
            return true
        }
    }

    abstract fun isValid(): Boolean
}

data class AddPhone(val name: String, val phone: String) : Command() {
    override fun isValid(): Boolean {
        return phone.matches(Regex("\\+\\d+"))
    }
}

data class AddEmail(val name: String, val email: String) : Command() {
    override fun isValid(): Boolean {
        return email.matches(Regex("\\w+@\\w+\\.\\w+"))
    }
}

data class Show(val person: Person?) : Command() {
    override fun isValid(): Boolean {
        return true
    }
}

data class Find(val value: String) : Command() {
    override fun isValid(): Boolean {
        return true
    }
}

data class Export(val filePath: String) : Command() {
    override fun isValid(): Boolean {
        return true // Добавьте здесь проверку на корректность пути к файлу, если необходимо
    }
}

data class Person(val name: String, var phones: MutableList<String> = mutableListOf(), var emails: MutableList<String> = mutableListOf())

class PhoneBook {
    private val contacts: MutableMap<String, Person> = mutableMapOf()

    fun addPerson(person: Person) {
        contacts[person.name] = person
    }

    fun addPhone(name: String, phone: String) {
        val person = contacts.getOrPut(name) { Person(name) }
        person.phones.add(phone)
    }

    fun addEmail(name: String, email: String) {
        val person = contacts.getOrPut(name) { Person(name) }
        person.emails.add(email)
    }

    fun show(name: String) {
        contacts[name]?.let { person ->
            println("Phones for ${person.name}: ${person.phones}")
            println("Emails for ${person.name}: ${person.emails}")
        } ?: println("Person with name $name not found")
    }

    fun find(value: String) {
        val foundPeople = contacts.filterValues { it.phones.contains(value) || it.emails.contains(value) }.keys
        if (foundPeople.isNotEmpty()) {
            println("People with ${if (value.contains('@')) "email" else "phone"} $value: $foundPeople")
        } else {
            println("No person found with $value")
        }
    }

    fun export(filePath: String) {
        val jsonString = json {
            "contacts" to contacts.values.map { person ->
                "name" to person.name
                "phones" to person.phones
                "emails" to person.emails
            }
        }
        File(filePath).writeText(jsonString)
        println("Exported data to $filePath")
    }
}

fun readCommand(): Command {
    print("Enter command: ")
    val input = readLine() ?: throw IllegalArgumentException("Input cannot be null")
    val tokens = input.trim().split(" ")
    return when {
        tokens.size >= 4 && tokens[0].equals("add", ignoreCase = true) && (tokens[2].equals("phone", ignoreCase = true) || tokens[2].equals("email", ignoreCase = true)) -> {
            val name = tokens[1]
            val value = tokens.subList(3, tokens.size).joinToString(" ")
            if (tokens[2].equals("phone", ignoreCase = true)) {
                AddPhone(name, value)
            } else {
                AddEmail(name, value)
            }
        }
        tokens.size >= 1 && tokens[0].equals("show", ignoreCase = true) -> {
            Show(null)
        }
        tokens.size >= 2 && tokens[0].equals("find", ignoreCase = true) -> {
            val value = tokens[1]
            Find(value)
        }
        tokens.size >= 2 && tokens[0].equals("export", ignoreCase = true) -> {
            val filePath = tokens[1]
            Export(filePath)
        }
        tokens.size >= 1 && tokens[0].equals("exit", ignoreCase = true) -> {
            Command.Exit
        }
        else -> throw IllegalArgumentException("Invalid command")
    }
}


class JsonObjectBuilder {
    private val map = LinkedHashMap<String, Any?>()

    fun add(key: String, value: Any?) {
        map[key] = value
    }

    fun add(init: JsonObjectBuilder.() -> Unit) {
        init()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("{")
        val iterator = map.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            stringBuilder.append("\"$key\":${formatValue(value)}")
            if (iterator.hasNext()) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append("}")
        return stringBuilder.toString()
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            is String -> "\"$value\""
            is Number, is Boolean -> value.toString()
            is Collection<*> -> "[" + value.joinToString(",") { formatValue(it) } + "]"
            is Map<*, *> -> JsonObjectBuilder().apply { value.forEach { (k, v) -> k?.toString()?.let { key -> this.add(key, v) } } }.toString()
            null -> "null"
            else -> throw IllegalArgumentException("Unsupported type: ${value?.javaClass}")
        }
    }
}

fun json(init: JsonObjectBuilder.() -> Unit): String {
    val builder = JsonObjectBuilder()
    builder.init()
    return builder.toString()
}


fun main() {

    val fileName = "phonebook.json"
    val fileContent = ""

    // Создание файла
    val file = File(fileName)
    file.createNewFile()

    val phoneBook = PhoneBook()

    var exit = false
    while (!exit) {
        try {
            val command = readCommand()
            println("Received command: $command")

            when (command) {
                is AddPhone -> {
                    if (command.isValid()) {
                        phoneBook.addPhone(command.name, command.phone)
                    } else {
                        println("Error: The entered phone number does not match the format")
                    }
                }
                is AddEmail -> {
                    if (command.isValid()) {
                        phoneBook.addEmail(command.name, command.email)
                    } else {
                        println("Error: The entered email address does not match the format")
                    }
                }
                is Show -> {
                    command.person?.name?.let { phoneBook.show(it) }
                }
                is Find -> {
                    if (command.isValid()) {
                        phoneBook.find(command.value)
                    } else {
                        println("Error: Invalid value for searching")
                    }
                }
                is Export -> {
                    if (command.isValid()) {
                        phoneBook.export(command.filePath)
                    } else {
                        println("Error: Invalid file path")
                    }
                }
                is Command.Exit -> {
                    exit = true
                    println("Exiting...")
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
