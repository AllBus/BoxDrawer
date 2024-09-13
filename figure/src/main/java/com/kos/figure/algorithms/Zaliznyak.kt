package com.kos.figure.algorithms

import java.util.regex.Pattern

class Zaliznyak {
}

data class ZaliznyakWord(
    val lemma: String,
    val partOfSpeech: PartOfSpeech,
    val gender: Gender? = null,
    val number: Number? = null,
    val case: Case? = null,
    val animation: Animation? = null,
    val declensionType: DeclensionType? = null,
    val conjugationType: ConjugationType? = null
) {
    enum class PartOfSpeech { NOUN, ADJECTIVE, PRONOUN, NUMERAL, VERB, ADVERB, PREPOSITION, CONJUNCTION, PARTICLE, INTERJECTION }
    enum class Gender { MASCULINE, FEMININE, NEUTER }
    enum class Number { SINGULAR, PLURAL }
    enum class Case { NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, INSTRUMENTAL, PREPOSITIONAL }
    enum class Animation { ANIMATE, INANIMATE }
    enum class DeclensionType { FIRST, SECOND, THIRD, ADJECTIVAL, PRONOMINAL, NUMERAL }
    enum class ConjugationType { FIRST, SECOND, IRREGULAR }

    override fun toString(): String {
        val properties = mutableListOf<String>()
        if (gender != null) properties.add("Gender: $gender")
        if (number != null) properties.add("Number: $number")
        if (case != null) properties.add("Case: $case")
        if (animation != null) properties.add("Animation: $animation")
        if (declensionType != null) properties.add("Declension: $declensionType")
        if (conjugationType != null) properties.add("Conjugation: $conjugationType")

        return "Lemma: $lemma, Part of Speech: $partOfSpeech, ${properties.joinToString(", ")}"
    }
}

// ... (ZaliznyakWord class from previous response)
object ZaliznyakConverter {

    // Define patterns for extracting grammatical information (adjust as needed)
    private val genderPattern = Pattern.compile("м|ж|с")
    private val numberPattern = Pattern.compile("ед|мн")
    private val casePattern = Pattern.compile("им|род|дат|вин|твор|пр")
    private val animationPattern = Pattern.compile("од|неод")
    private val declensionPattern = Pattern.compile("I|II|III|п|мс|числ")
    private val conjugationPattern = Pattern.compile("I|II|разноспр")

    fun convert(zaliznyakNotation: String): ZaliznyakWord {
        val parts = zaliznyakNotation.split(",")

        val lemma = parts[0].trim()
        val partOfSpeech = parsePartOfSpeech(parts[1].trim())

        var gender: ZaliznyakWord.Gender? = null
        var number: ZaliznyakWord.Number? = null
        var case: ZaliznyakWord.Case? = null
        var animation: ZaliznyakWord.Animation? = null
        var declensionType: ZaliznyakWord.DeclensionType? = null
        var conjugationType: ZaliznyakWord.ConjugationType? = null

        for (part in parts.drop(2)) {
            val trimmedPart = part.trim()
            when {
                genderPattern.matcher(trimmedPart).matches() -> gender = parseGender(trimmedPart)
                numberPattern.matcher(trimmedPart).matches() -> number = parseNumber(trimmedPart)
                casePattern.matcher(trimmedPart).matches() -> case = parseCase(trimmedPart)
                animationPattern.matcher(trimmedPart).matches() -> animation =
                    parseAnimation(trimmedPart)

                declensionPattern.matcher(trimmedPart).matches() -> declensionType =
                    parseDeclensionType(trimmedPart)

                conjugationPattern.matcher(trimmedPart).matches() -> conjugationType =
                    parseConjugationType(trimmedPart)
            }
        }

        return ZaliznyakWord(
            lemma,
            partOfSpeech,
            gender,
            number,
            case,
            animation,
            declensionType,
            conjugationType
        )
    }

    private fun parsePartOfSpeech(notation: String): ZaliznyakWord.PartOfSpeech {
        return when (notation) {
            "сущ" -> ZaliznyakWord.PartOfSpeech.NOUN
            "прил" -> ZaliznyakWord.PartOfSpeech.ADJECTIVE
            "мест" -> ZaliznyakWord.PartOfSpeech.PRONOUN
            "числ" -> ZaliznyakWord.PartOfSpeech.NUMERAL
            "гл" -> ZaliznyakWord.PartOfSpeech.VERB
            "нареч" -> ZaliznyakWord.PartOfSpeech.ADVERB
            "предл" -> ZaliznyakWord.PartOfSpeech.PREPOSITION
            "союз" -> ZaliznyakWord.PartOfSpeech.CONJUNCTION
            "част" -> ZaliznyakWord.PartOfSpeech.PARTICLE
            "межд" -> ZaliznyakWord.PartOfSpeech.INTERJECTION
            else -> throw IllegalArgumentException("Unknown part of speech notation: $notation")
        }
    }

    private fun parseGender(notation: String): ZaliznyakWord.Gender {
        return when (notation) {
            "м" -> ZaliznyakWord.Gender.MASCULINE
            "ж" -> ZaliznyakWord.Gender.FEMININE
            "с" -> ZaliznyakWord.Gender.NEUTER
            else -> throw IllegalArgumentException("Unknown gender notation: $notation")
        }
    }

    private fun parseNumber(notation: String): ZaliznyakWord.Number {
        return when (notation) {
            "ед" -> ZaliznyakWord.Number.SINGULAR
            "мн" -> ZaliznyakWord.Number.PLURAL
            else -> throw IllegalArgumentException("Unknown number notation: $notation")
        }
    }

    private fun parseCase(notation: String): ZaliznyakWord.Case {
        return when (notation) {
            "им" -> ZaliznyakWord.Case.NOMINATIVE
            "род" -> ZaliznyakWord.Case.GENITIVE
            "дат" -> ZaliznyakWord.Case.DATIVE
            "вин" -> ZaliznyakWord.Case.ACCUSATIVE
            "твор" -> ZaliznyakWord.Case.INSTRUMENTAL
            "пр" -> ZaliznyakWord.Case.PREPOSITIONAL
            else -> throw IllegalArgumentException("Unknown case notation: $notation")
        }
    }

    private fun parseAnimation(notation: String): ZaliznyakWord.Animation {
        return when (notation) {
            "од" -> ZaliznyakWord.Animation.ANIMATE
            "неод" -> ZaliznyakWord.Animation.INANIMATE
            else -> throw IllegalArgumentException("Unknown animation notation: $notation")
        }
    }

    private fun parseDeclensionType(notation: String): ZaliznyakWord.DeclensionType {
        return when (notation) {
            "I" -> ZaliznyakWord.DeclensionType.FIRST
            "II" -> ZaliznyakWord.DeclensionType.SECOND
            "III" -> ZaliznyakWord.DeclensionType.THIRD
            "п" -> ZaliznyakWord.DeclensionType.ADJECTIVAL
            "мс" -> ZaliznyakWord.DeclensionType.PRONOMINAL
            "числ" -> ZaliznyakWord.DeclensionType.NUMERAL
            else -> throw IllegalArgumentException("Unknown declension type notation: $notation")
        }
    }

    private fun parseConjugationType(notation: String): ZaliznyakWord.ConjugationType {
        return when (notation) {
            "I" -> ZaliznyakWord.ConjugationType.FIRST
            "II" -> ZaliznyakWord.ConjugationType.SECOND
            "разноспр" -> ZaliznyakWord.ConjugationType.IRREGULAR
            else -> throw IllegalArgumentException("Unknown conjugation type notation: $notation")
        }
    }

}

