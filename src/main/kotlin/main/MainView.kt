package main

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import javafx.stage.Stage
import tornadofx.*

class MainView : View() {
    private val controller: MainController by inject()
    private val input = SimpleStringProperty()
    private val output = SimpleStringProperty()

    override val root = vbox {
        textfield(input)

        button("Получить") {
            action {
                //controller.sendMessage()
                controller.getPassword(input.get(), output)
            }
        }

        listview(controller.getLogins()) {
            onUserSelect(1) {
                controller.getLogin(it, output)
            }

            onUserSelect(2) {
                controller.getPassword(it, output)
            }
        }

        textfield(output)
    }
}

class TornadoApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.minWidth = 200.0
        stage.minHeight = 100.0
        stage.isAlwaysOnTop = true
        super.start(stage)
    }
}

fun main(args: Array<String>) = run{
    launch<TornadoApp>(args)
}

class MainController: Controller() {
    private val passwordMap = HashMap<String, String>()
    private val loginList = ArrayList<String>()

    fun getPassword(input: String, output: SimpleStringProperty) {
        if (passwordMap.isEmpty()) {
            passwordMap.put("ftest", "ftest123")
            passwordMap.put("support", "M5gisgKL")
            passwordMap.put("ar2", "123321")
            passwordMap.put("abarinov", "Abarinov")
        }
        val currentPassword = passwordMap.getOrDefault(input, "Не найдено совпадений")
        output.set(currentPassword)
        val clipboard = Clipboard.getSystemClipboard()
        clipboard.putString(currentPassword)
    }

    fun getLogin(input: String, output: SimpleStringProperty) {
        output.set(input)
        val clipboard = Clipboard.getSystemClipboard()
        clipboard.putString(input)
    }

    fun getLogins() : ObservableList<String> {
        loginList.add("ftest")
        loginList.add("support")
        loginList.add("ar2")
        loginList.add("abarinov")
        return FXCollections.observableArrayList(loginList)
    }

    /**
     * Что планируется сделать:
     --1. Собрать jar
     * 2. Загрузка из файла
     * 3. Сохранение в файл
     * 4. Сортировка по алфавиту
     * 5. Поиск
     * 6. Добавление избранного
     * */
}