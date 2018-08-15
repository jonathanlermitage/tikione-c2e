package fr.tikione.c2e.core.model.home

import java.io.File

class MagazineSummary {

    lateinit var number: String
    lateinit var options: String
    lateinit var file: File
    lateinit var humanSize: String
    lateinit var coverAsBase64: String

    override fun toString(): String {
        return "MagazineSummary(number='$number', options='$options', file=$file, humanSize='$humanSize')"
    }
}
