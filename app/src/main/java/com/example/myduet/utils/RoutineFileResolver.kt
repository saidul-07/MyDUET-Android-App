package com.example.myduet.utils

import java.util.Locale

object RoutineFileResolver {

    fun getAssetPath(department: String, year: String, section: String): String {
        val deptCode = department.lowercase(Locale.ROOT)
            .replace("&", "")
            .replace("engineering", "")
            .trim()
            .split(" ")[0] // Take only the first word/abbreviation
            .let { 
                when {
                    it.contains("computer") || it == "cse" -> "cse"
                    it.contains("electrical") || it == "eee" -> "eee"
                    it.contains("civil") || it == "ce" -> "ce"
                    it.contains("mechanical") || it == "me" -> "me"
                    it.contains("textile") || it == "te" -> "te"
                    it.contains("industrial") || it == "ipe" -> "ipe"
                    it.contains("architecture") || it == "arch" -> "arch"
                    it.contains("food") || it == "fe" -> "fe"
                    else -> it
                }
            }

        val yearCode = when {
            year.contains("1st") -> "first_year"
            year.contains("2nd") -> "second_year"
            year.contains("3rd") -> "third_year"
            year.contains("4th") -> "fourth_year"
            else -> "first_year"
        }

        val sectionCode = if (section.contains("B", ignoreCase = true)) "sec_b" else "sec_a"

        // Format: routines/cse/cse_third_year_sec_a.json
        return "routines/$deptCode/${deptCode}_${yearCode}_${sectionCode}.json"
    }
}