package cz.pm2k.swaply.model

import com.opencsv.bean.CsvBindByName
import cz.pm2k.swaply.config.CsvConfig
import cz.pm2k.swaply.helper.NoArg

@NoArg
data class ExchangeRateCsvRow(

    @CsvBindByName(column = "země", required = true)
    var country: String,

    @CsvBindByName(column = "měna", required = true)
    var currency: String,

    @CsvBindByName(column = "množství", required = true)
    var amount: Int,

    @CsvBindByName(column = "kód", required = true)
    var code: String,

    @CsvBindByName(column = "kurz", required = true, locale = CsvConfig.LOCALE_CS_CZ)
    var rate: Double,

)
