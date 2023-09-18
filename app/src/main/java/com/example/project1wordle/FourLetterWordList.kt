package com.example.project1wordle

import java.util.Random

class FourLetterWordList {
    private val words = listOf("quiz", "wall", "pick", "lash", "cute")

    fun getRandomFourLetterWord(): String {
        val random = Random()
        val randomIndex = random.nextInt(words.size)
        return words[randomIndex]
    }

    fun getTargetWord(): String {
        return getRandomFourLetterWord()
    }
}
