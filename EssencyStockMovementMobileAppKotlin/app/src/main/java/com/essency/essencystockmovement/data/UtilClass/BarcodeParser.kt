package com.essency.essencystockmovement.data.UtilClass

import com.essency.essencystockmovement.data.model.BarcodeData

class BarcodeParser {

    private val regex = Regex(
        "^(00(?<Pallet>\\d+)[|]8010(?<PartNumberWH1>[A-Za-z]\\d+)[_]92(?<RevWH1>[A-Za-z])[_]37(?<CountOfTradeItemsWH1>\\d+)[_]11(?<ProductionDateWH1>\\d+)[_]424(?<CountryOfPoductionWH1>\\d+)[_]91(?<SerialNumberOfTheProductWH1>\\d+[A-Za-z]\\d+)[|]8010(?<PartNumberWH2>[A-Za-z]\\d+)[_]92(?<RevWH2>[A-Za-z])[_]37(?<CountOfTradeItemsWH2>\\d+)[_]11(?<ProductionDateWH2>\\d+)[_]424(?<CountryOfPoductionWH2>\\d+)[_]91(?<SerialNumberOfTheProductWH2>\\d+[A-Za-z]\\d+)\$)|^(8010(?<PartNumber>[A-Za-z]\\d+)[_]92(?<Rev>[A-Za-z])[_]37(?<CountOfTradeItems>\\d+)[_]11(?<ProductionDate>\\d+)[_]424(?<CountryOfPoduction>\\d+)[_]91(?<SerialNumberOfTheProduct>\\d+[A-Za-z]\\d+))\$"
    )

    fun parseBarcode(barcode: String): BarcodeData? {
        val match = regex.matchEntire(barcode) ?: return null

        return BarcodeData(
            pallet = match.groups["Pallet"]?.value,
            partNumberWH1 = match.groups["PartNumberWH1"]?.value,
            revWH1 = match.groups["RevWH1"]?.value,
            countOfTradeItemsWH1 = match.groups["CountOfTradeItemsWH1"]?.value?.toIntOrNull(),
            productionDateWH1 = match.groups["ProductionDateWH1"]?.value,
            countryOfProductionWH1 = match.groups["CountryOfPoductionWH1"]?.value,
            serialNumberWH1 = match.groups["SerialNumberOfTheProductWH1"]?.value,
            partNumberWH2 = match.groups["PartNumberWH2"]?.value,
            revWH2 = match.groups["RevWH2"]?.value,
            countOfTradeItemsWH2 = match.groups["CountOfTradeItemsWH2"]?.value?.toIntOrNull(),
            productionDateWH2 = match.groups["ProductionDateWH2"]?.value,
            countryOfProductionWH2 = match.groups["CountryOfPoductionWH2"]?.value,
            serialNumberWH2 = match.groups["SerialNumberOfTheProductWH2"]?.value,
            partNumber = match.groups["PartNumber"]?.value,
            rev = match.groups["Rev"]?.value,
            countOfTradeItems = match.groups["CountOfTradeItems"]?.value?.toIntOrNull(),
            productionDate = match.groups["ProductionDate"]?.value,
            countryOfProduction = match.groups["CountryOfPoduction"]?.value,
            serialNumber = match.groups["SerialNumberOfTheProduct"]?.value
        )
    }
}