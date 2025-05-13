package cz.pm2k.swaply.helper

import com.opencsv.bean.CsvToBeanBuilder
import mu.KotlinLogging
import java.io.StringReader

annotation class NoArg

private val logger = KotlinLogging.logger {}

/**
 * Reads all lines from CSV at once
 */
fun <T> readCsv(
    csvContent: String,
    returnType: Class<T>,
    skipLines: Int = 0,
    separator: Char = '|',
) : List<T> {
    StringReader(csvContent).use {
        val parser = CsvToBeanBuilder<T>(it)
            .withType(returnType)
            .withSeparator(separator)
            .withSkipLines(skipLines)
            .withIgnoreLeadingWhiteSpace(true)
            .build()
        return parser.parse().also { result ->
            logger.debug("Parsed ${result.size} rows from CSV")
        }
    }
}