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

data class Person(val name: String, var phone: String? = null, var email: String? = null)

fun readCommand(): Command {
    print("Введите команду: ")
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
        else -> throw IllegalArgumentException("Некорректная команда")
    }
}

fun main() {
    var currentPerson: Person? = null

    while (true) {
        try {
            val command = readCommand()
            println("Получена команда: $command")
            when (command) {
                is AddPhone -> {
                    if (command.isValid()) {
                        currentPerson = currentPerson ?: Person(command.name)
                        currentPerson.phone = command.phone
                    } else {
                        println("Ошибка: Введенный номер телефона не соответствует формату")
                    }
                }
                is AddEmail -> {
                    if (command.isValid()) {
                        currentPerson = currentPerson ?: Person(command.name)
                        currentPerson.email = command.email
                    } else {
                        println("Ошибка: Введенный адрес электронной почты не соответствует формату")
                    }
                }
                is Show -> {
                    if (currentPerson != null) {
                        println("Последний введенный контакт: ${currentPerson.name}, ${currentPerson.phone ?: ""}, ${currentPerson.email ?: ""}")
                    } else {
                        println("Not initialized")
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
