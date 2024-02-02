sealed class Command {
    abstract fun isValid(): Boolean
}

data class AddPhone(val name: String, val phone: String): Command() {
    override fun isValid(): Boolean {
        return phone.matches(Regex("\\+\\d+"))
    }
}

data class AddEmail(val name: String, val email: String): Command() {
    override fun isValid(): Boolean {
        return email.matches(Regex("\\w+@\\w+\\.\\w+"))
    }
}

data class Show(val person: Person?): Command() {
    override fun isValid(): Boolean {
        return true
    }
}

data class Find(val value: String) : Command() {
    override fun isValid(): Boolean {
        return true
    }
}

data class Person(val name: String, var phones: MutableList<String> = mutableListOf(), var emails: MutableList<String> = mutableListOf())

class PhoneBook {
    private val contacts: MutableMap<String, Person> = mutableMapOf()

    fun addPerson(person: Person) {
        contacts[person.name] = person
    }

    fun addPhone(name: String, phone: String) {
        contacts[name]?.phones?.add(phone) ?: println("Person with name $name not found")
    }

    fun addEmail(name: String, email: String) {
        contacts[name]?.emails?.add(email) ?: println("Person with name $name not found")
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
}

fun readCommand(): Command {
    print("Enter command: ")
    val input = readLine()
    val tokens = input?.trim()?.split(" ") ?: emptyList()
    return when {
        tokens.size >= 4 && tokens[0].equals("add", ignoreCase = true) && tokens[2].equals("phone", ignoreCase = true) -> {
            val name = tokens[1]
            val phone = tokens.subList(3, tokens.size).joinToString(" ")
            AddPhone(name, phone)
        }
        tokens.size >= 4 && tokens[0].equals("add", ignoreCase = true) && tokens[2].equals("email", ignoreCase = true) -> {
            val name = tokens[1]
            val email = tokens.subList(3, tokens.size).joinToString(" ")
            AddEmail(name, email)
        }
        tokens.size >= 1 && tokens[0].equals("show", ignoreCase = true) -> {
            Show(null)
        }
        tokens.size >= 2 && tokens[0].equals("find", ignoreCase = true) -> {
            val value = tokens[1]
            Find(value)
        }
        else -> throw IllegalArgumentException("Invalid command")
    }
}

fun main() {
    val phoneBook = PhoneBook()

    while (true) {
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
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}