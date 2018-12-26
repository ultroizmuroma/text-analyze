package main

import com.mongodb.client.FindIterable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.Clipboard
import javafx.stage.Stage
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import tornadofx.*
import java.util.stream.Collectors

class MainView : View() {
    private val controller: MainController by inject()
    private val login = SimpleStringProperty()
    private val password = SimpleStringProperty()
    private val output = SimpleStringProperty()
    private val search = SimpleStringProperty();

    private var searchTextField: TextField by singleAssign()
    private var loginsList: ListView<String> by singleAssign()

    override val root = vbox {
        gridpane {
            row {
                label("Логин")

                label("Пароль")
            }

            row {
                textfield(login)

                textfield(password)

                button("Добавить") {
                    action {
                        controller.addUser(login.get(), password.get())
                        loginsList.items = controller.getLogins()
                        login.set("")
                        password.set("")
                    }
                }
            }
        }

        vbox {
            label("Поиск")

            searchTextField = textfield(search) {
                setOnKeyReleased { loginsList.items = FXCollections.observableArrayList(controller.search(search.get())) }
            }
        }

        vbox {
            label ("Список пользователей")
            loginsList = listview {
                id = "mainList"
                onUserSelect(1) {
                    controller.getLogin(it, output)
                }

                onUserSelect(2) {
                    controller.getPassword(it, output)
                }
            }
        }

        label("Буфер обмена")
        textfield(output)
    }

    init {
        loginsList.items = controller.getLogins()
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
        refreshLists()
        return FXCollections.observableArrayList(loginList)
    }

    fun refreshLists() {
        loginList.clear()
        passwordMap.clear()

        for (item in getFromDb()) {
            val login = item.login
            val password = item.password
            loginList.add(login)
            passwordMap[login] = password
        }
    }

    fun getFromDb(): FindIterable<TestUser> {
        val client = KMongo.createClient("10.10.80.20", 27017)
        val database = client.getDatabase("mfc-stage")
        val collection = database.getCollection<TestUser>("test_user")

        return collection.find()
    }

    fun addUser(login: String, password: String) {
        val client = KMongo.createClient("10.10.80.20", 27017)
        val database = client.getDatabase("mfc-stage")
        val collection = database.getCollection<TestUser>("test_user")
        collection.insertOne(TestUser(login, password))
    }

    fun search(searchString: String): List<String> {
        if (searchString.isEmpty()) {
           return loginList
        }
        return loginList.stream().filter{item -> item.contains(searchString)}.collect(Collectors.toList())
    }

    /**
     * Что планируется сделать:
     --1. Собрать jar
     --2. Загрузка из файла
     * 2.1. Обновление записей
     --3. Сохранение в файл
     * 3.1. Очистка полей после сохранения
     *
     * 4. Сортировка по алфавиту
     * 5. Поиск
     * 6. Добавление избранного
     * */
}