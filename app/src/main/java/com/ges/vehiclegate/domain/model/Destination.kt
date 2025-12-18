package com.ges.vehiclegate.domain.model

enum class Destination(val label: String) {
    ENTREPOT("Entrepôt"),
    ATELIER("Atelier"),
    BUREAUX("Bureaux"),
    QUAI_A("Quai A"),
    QUAI_B("Quai B"),
    AUTRE("Autre");

    companion object {
        fun fromLabel(label: String): Destination =
            entries.firstOrNull { it.label == label } ?: AUTRE
    }
}
