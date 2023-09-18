package com.example.project1wordle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.project1wordle.FourLetterWordList
import java.util.Random
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.KonfettiView

class MainActivity : AppCompatActivity() {

    private lateinit var viewKonfetti: KonfettiView
    private lateinit var guessEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var targetWordTextView: TextView
    private lateinit var streakCounter: TextView
    private lateinit var messageTextView: TextView
    private lateinit var guess1TextView: TextView
    private lateinit var result1TextView: TextView
    private lateinit var guess2TextView: TextView
    private lateinit var result2TextView: TextView
    private lateinit var guess3TextView: TextView
    private lateinit var result3TextView: TextView

    private var currentGuess = 1
    private var streakCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guessEditText = findViewById(R.id.guessEditText)
        submitButton = findViewById(R.id.submitButton)
        targetWordTextView = findViewById(R.id.targetWord)
        streakCounter = findViewById(R.id.streakCounter)
        messageTextView = findViewById(R.id.messageText)
        guess1TextView = findViewById(R.id.guess1TextView)
        result1TextView = findViewById(R.id.result1TextView)
        guess2TextView = findViewById(R.id.guess2TextView)
        result2TextView = findViewById(R.id.result2TextView)
        guess3TextView = findViewById(R.id.guess3TextView)
        result3TextView = findViewById(R.id.result3TextView)

        val fourLetterWord = FourLetterWordList()
        var targetWord = fourLetterWord.getRandomFourLetterWord()

        // ================== RESET BUTTON EVENTS ==================
        val resetButton = findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            val newFourLetterWord = fourLetterWord.getRandomFourLetterWord()

            guess1TextView.text = "----"
            result1TextView.text = "----"
            guess2TextView.text = "----"
            result2TextView.text = "----"
            guess3TextView.text = "----"
            result3TextView.text = "----"
            currentGuess = 1
            targetWordTextView.text = ""

            submitButton.isEnabled = true  // Turn the submitButton function off
            targetWord = newFourLetterWord // Set the new target word
            messageTextView.text = "Please make your first guess!" // Set new text message for user
        }

        // ================== SUBMIT BUTTON EVENTS ==================
        submitButton.setOnClickListener {
            val guess = guessEditText.text.toString().trim()
            val guessResult = handleGuess(guess, targetWord, currentGuess)
            val konfettiView = findViewById<KonfettiView>(R.id.viewKonfetti)

            // Update TextView after every guess
            when (currentGuess) {
                1 -> {
                    guess1TextView.text = "$guess"
                    result1TextView.text = guessResult.coloredResult
                }
                2 -> {
                    guess2TextView.text = "$guess"
                    result2TextView.text = guessResult.coloredResult
                }
                3 -> {
                    guess3TextView.text = "$guess"
                    result3TextView.text = guessResult.coloredResult
                }
            }

            // Update streak counter
            if (currentGuess <= 3 && guessResult.correctness == "CCCC") {
                streakCount++
                streakCounter.text = "$streakCount"
                // Conditions for error message
                messageTextView.text = "Fantastic!"
                // Display the target word
                targetWordTextView.text = "$targetWord"
                submitButton.isEnabled = false

                // Trigger confetti effects
                konfettiView.build()
                    .addColors(Color.RED, Color.BLACK, Color.BLUE)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12))
                    .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                    .streamFor(300, 2000L)
            }

            // Conditions for error message
            if (currentGuess == 3 && guessResult.correctness != "CCCC") {
                messageTextView.text = "Try again..."
                if (streakCount != 0) {
                    streakCount = 0
                    streakCounter.text = "0"
                    messageTextView.text = "Oh no you lost your streak!"
                }
            }
            if (currentGuess == 3) {
                // Display the target word
                targetWordTextView.text = "$targetWord"
                submitButton.isEnabled = false
            }
            if (currentGuess <= 3 && guessResult.correctness.contains("L")) {
                messageTextView.text = "Orange letters are correct just in the wrong position"
            }

            currentGuess++

            // Clear the EditText
            guessEditText.text.clear()

        }
    }

    data class GuessResult(val correctness: String, val coloredResult: SpannableString)

    private fun handleGuess(guess: String, targetWord: String, guessCount: Int): GuessResult {
        // This function updates the resultTextView
        val correctness = calculateCorrectness(guess, targetWord)
        val coloredResult = SpannableString(guess)

        for (i in guess.indices) {
            val color: Int = when (correctness[i]) {
                'C' -> Color.GREEN
                'L' -> Color.parseColor("#FFA500") // Orange
                'W' -> Color.RED
                else -> Color.BLACK
            }

            coloredResult.setSpan(
                ForegroundColorSpan(color),
                i, i + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        return GuessResult(correctness, coloredResult)
    }


    private fun calculateCorrectness(guess: String, targetWord: String): String {
        // This functions return the correctness of each letter in the four-letter word
        val correctness = StringBuilder()
        for (i in guess.indices) {
            if (guess[i] == targetWord[i]) {
                correctness.append('C') // correct letter, correct pos
            } else if (targetWord.contains(guess[i])) {
                correctness.append('L') // correct letter
            } else {
                correctness.append('W') // wrong letter
            }
        }
        return correctness.toString()
    }


}

