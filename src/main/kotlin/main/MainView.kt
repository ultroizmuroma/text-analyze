package main

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.stage.Stage
import tornadofx.*

class MainView : View() {
    private val controller: MainController by inject()

    private val textareaProperty = SimpleStringProperty("Yesterday all my troubles seemed so far away.\n" +
            "Now it looks as though they're here to stay.\n" +
            "Oh, I believe in yesterday.\n" +
            "\n" +
            "Suddenly, I'm not half the man I used to be.\n" +
            "There's a shadow hanging over me.\n" +
            "Oh, yesterday came suddenly.\n" +
            "\n" +
            "Why she had to go?\n" +
            "I don't know, she wouldn't say.\n" +
            "I said something wrong.\n" +
            "Now I long for yesterday.\n" +
            "\n" +
            "Yesterday love was such an easy game to play.\n" +
            "Now I need a place to hide away.\n" +
            "Oh, I believe in yesterday.\n" +
            "\n" +
            "Why she had to go?\n" +
            "I don't know, she wouldn't say.\n" +
            "I said something wrong.\n" +
            "Now I long for yesterday.\n" +
            "\n" +
            "Yesterday love was such an easy game to play.\n" +
            "Now I need a place to hide away.\n" +
            "Oh, I believe in yesterday")

    private val listProperty = SimpleListProperty<String>()
    private val count = SimpleStringProperty("")


    override val root = vbox {
        textarea(textareaProperty) {
            selectAll()
        }

        button("Анализировать") {
            action {
                controller.doAction(textareaProperty, listProperty, count)
            }
        }

        listview<String>(listProperty)
        hbox {
            label("Количество уникальных слов: ")
            label(count)
        }

    }
}

class TornadoApp : App(MainView::class) {
    override fun start(stage: Stage) {
        stage.minWidth = 400.0
        stage.minHeight = 400.0
        super.start(stage)
    }
}

fun main(args: Array<String>) = run{
    launch<TornadoApp>(args)
}

class MainController: Controller() {
    fun doAction(input: SimpleStringProperty, output: SimpleListProperty<String>, count: SimpleStringProperty) {
        var text = input.get().replace("\n", " ")
        text = text.replace(".", " ")
        text = text.replace(",", " ")
        text = text.replace("?", " ")
        text = text.replace("     ", " ")
        text = text.replace("    ", " ")
        text = text.replace("   ", " ")
        text = text.replace("  ", " ")
        val list = text.split(" ")
        val hashMap: HashMap<String, Int> = HashMap()
        for (item in list) {
            val count = hashMap[item] ?: 0
            hashMap[item] = count + 1
        }
        val map = hashMap.toList().sortedBy { (_, value) -> -1 * value}.toMap()

        val newList = ArrayList<String>()
        for (item in map) {
            newList.add(item.key + " " + item.value)
        }
        output.set(FXCollections.observableArrayList(newList))
        count.set(newList.size.toString())
    }
}