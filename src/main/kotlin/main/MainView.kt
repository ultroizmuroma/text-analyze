package main

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import javafx.stage.Stage
import tornadofx.*
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

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

    fun sendMessage() {
        var urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s"

        val apiToken = "706235916:AAEb7tSyepct-kWzosQx5XYw7sJ1JIVV4WQ"
        val chatId = "@eisou"
        val text = "Hello world!"

        urlString = String.format(urlString, apiToken, chatId, text)

        val url = URL(urlString)
        val conn = url.openConnection()

        val inputStream = BufferedInputStream(conn.getInputStream())
        val br = BufferedReader(InputStreamReader(inputStream))

        val response = br.readText()
// Do what you want with response
    }
}