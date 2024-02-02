fun main() {
    val contacts = mutableMapOf<String, MutableMap<String, String>>()

    while (true) {
        print("Введите команду: ")
        val input = readLine()

        when {
            input.equals("exit", ignoreCase = true) -> break
            input.equals("help", ignoreCase = true) -> {
                println("Доступные команды: exit, help, add <Имя> phone <Номер телефона>, add <Имя> email <Адрес электронной почты>")
            }
            input?.startsWith("add", ignoreCase = true) == true -> {
                val tokens = input.trim().split(" ")
                if (tokens.size >= 4) {
                    val name = tokens[1]
                    val type = tokens[2]
                    val value = tokens.subList(3, tokens.size).joinToString(" ")

                    when {
                        type.equals("phone", ignoreCase = true) -> {
                            if (value.matches(Regex("\\+\\d+"))) {
                                contacts.getOrPut(name) { mutableMapOf() }[type] = value
                                println("Контакт $name: номер телефона $value")
                            } else {
                                println("Ошибка: Введенный номер телефона не соответствует формату")
                            }
                        }
                        type.equals("email", ignoreCase = true) -> {
                            if (value.matches(Regex("\\w+@\\w+\\.\\w+"))) {
                                contacts.getOrPut(name) { mutableMapOf() }[type] = value
                                println("Контакт $name: адрес электронной почты $value")
                            } else {
                                println("Ошибка: Введенный адрес электронной почты не соответствует формату")
                            }
                        }
                    }
                } else {
                    println("Неверный формат команды")
                }
            }
            else -> {
                println("Неизвестная команда")
            }
        }
    }
}