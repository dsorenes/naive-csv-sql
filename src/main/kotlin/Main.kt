import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("sql-scv");

    val query by parser.argument(ArgType.String, description = "Input query")

    parser.parse(args)

    val parsedQuery = parseQuery(query)
    val inputData = readFile(parsedQuery.table)

    val data: MutableList<String> = inputData.split("\n").toMutableList()
    data.removeLast()

    val columns: Map<String, Int> = data.removeAt(0)
        .split(",")
        .mapIndexed { index: Int, columnName: String -> columnName to index }
        .toMap()
    println(columns)
    println("---------------------------")
    // SELECT Aktivitetstype, Avstand FROM 'Activities.csv'
    val indexes = parsedQuery.columns.map { columns.getValue(it) }

    val values = data.map {
        it.split(",").slice(indexes)
    }

    println(values)
}

fun parseQuery(query: String): Query {
    val arr = query.split(" ").toMutableList()
    val type = arr.removeAt(0).uppercase()

    when(type) {
        "SELECT" -> {
            val columns = arr.map { it.replace(",", "") }
                .takeWhile { it != "FROM" }
            val table = arr.last().replace('\'', ' ').trim()

            return Query("SELECT", columns, table)
        }
    }
    return Query()
}

data class Query(val type: String = "", val columns: List<String> = emptyList(), val table: String = "")

data class Column(val name: String, val index: Int)

fun readFile(inputName: String): String {
    val bufferedReader: BufferedReader = File(inputName).bufferedReader()

    return bufferedReader.use { it.readText() }

}