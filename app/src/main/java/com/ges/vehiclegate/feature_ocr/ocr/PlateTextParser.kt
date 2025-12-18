package com.ges.vehiclegate.feature_ocr.ocr

object PlateTextParser {

    // Liste officielle des codes départements métropole 01–976 (on simplifie ici)
    private val departmentCodes = listOf(
        "01","02","03","04","05","06","07","08","09",
        "10","11","12","13","14","15","16","17","18","19",
        "2A","2B",
        "21","22","23","24","25","26","27","28","29",
        "30","31","32","33","34","35","36","37","38","39",
        "40","41","42","43","44","45","46","47","48","49",
        "50","51","52","53","54","55","56","57","58","59",
        "60","61","62","63","64","65","66","67","68","69",
        "70","71","72","73","74","75","76","77","78","79",
        "80","81","82","83","84","85","86","87","88","89",
        "90","91","92","93","94","95","971","972","973","974","976"
    )

    fun extractBestPlate(raw: String): String? {
        val cleaned = normalize(raw)

        val noCountry = dropCountry(cleaned)
        val noDept = dropDepartment(noCountry)

        // Format moderne AA-123-AA
        val regex = Regex("""([A-Z]{2})(\d{3})([A-Z]{2})""")
        val m = regex.find(noDept) ?: return null

        val a = m.groupValues[1]
        val b = m.groupValues[2]
        val c = m.groupValues[3]

        return "$a-$b-$c"
    }

    private fun normalize(s: String): String {
        var t = s.uppercase()

        // Correction OCR
        t = t.replace('O', '0')
        t = t.replace('S', '5')
        t = t.replace('B', '8')
        t = t.replace('I', '1')
        t = t.replace('Z', '2')

        // Garde alphanum
        t = t.replace("[^A-Z0-9]".toRegex(), "")

        return t
    }

    private fun dropCountry(t: String): String {
        // F à gauche retiré si présent
        return if (t.startsWith("F")) t.drop(1) else t
    }

    private fun dropDepartment(t: String): String {
        // Enlever codes département à droite si trouvés
        departmentCodes.forEach { code ->
            if (t.endsWith(code)) return t.dropLast(code.length)
        }
        return t
    }
}
