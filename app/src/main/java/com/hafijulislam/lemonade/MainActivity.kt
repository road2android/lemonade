package com.hafijulislam.lemonade

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

/**
 * A Lemon tree class with a method to "pick" a lemon. The "size" of the lemon is randomized
 * and determines how many times a lemon needs to be squeezed before you get lemonade.
 */
class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}

class MainActivity : AppCompatActivity() {

    private val LEMONADE_STATE: String = "LEMONADE_STATE"

    private val LEMON_SIZE: String = "LEMON_SIZE"

    private val SQUEEZE_COUNT: String = "SQUEEZE_COUNT"

    // SELECT represents the "pick lemon" state
    private val SELECT = "select"

    // SQUEEZE represents the "squeeze lemon" state
    private val SQUEEZE = "squeeze"

    // DRINK represents the "drink lemonade" state
    private val DRINK = "drink"

    // RESTART represents the state where the lemonade has been drunk and the glass is empty
    private val RESTART = "restart"

    private var lemonadeState = "select"

    private var lemonSize = -1

    private var lemonImage: ImageView? = null

    private var squeezeCount = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }

        lemonImage = findViewById(R.id.image_lemon_state)

        lemonImage!!.setOnClickListener {
            clickLemonImage()
            setViewElements()
        }

        lemonImage!!.setOnLongClickListener {
            showSnackBar()
        }

        setViewElements()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    private fun clickLemonImage() {
        when {
            lemonadeState == SELECT -> {
                val lemonTree = LemonTree()
                lemonSize = lemonTree.pick()
                lemonadeState = SQUEEZE
                squeezeCount = 0
            }

            lemonadeState == SQUEEZE -> {
                if (squeezeCount < lemonSize) {
                    squeezeCount += 1
                } else {
                    lemonSize = -1
                    squeezeCount = -1
                    lemonadeState = DRINK
                }
            }

            lemonadeState == DRINK -> lemonadeState = RESTART

            lemonadeState == RESTART -> lemonadeState = SELECT
        }

        lemonImage?.contentDescription = lemonadeState

    }

    private fun setViewElements() {
        val lemonText: TextView = findViewById(R.id.text_action)

        (when (lemonadeState) {
            SELECT -> {
                lemonImage?.setImageResource(R.drawable.lemon_tree)
                lemonText.setText(getString(R.string.lemon_select))
            }
            SQUEEZE -> {
                lemonImage?.setImageResource(R.drawable.lemon_squeeze)
                lemonText.setText(getString(R.string.lemon_squeeze))
                if (squeezeCount == 0) {
                    Toast.makeText(
                        this,
                        getString(R.string.squeeze_start, lemonSize),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (squeezeCount in 1 until lemonSize) {
                    Toast.makeText(
                        this,
                        getString(R.string.squeeze_count, squeezeCount),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            DRINK -> {
                lemonImage?.setImageResource(R.drawable.lemon_drink)
                lemonText.setText(getString(R.string.lemon_drink))
                Toast.makeText(
                    this,
                    getString(R.string.lemon_serve),
                    Toast.LENGTH_SHORT
                ).show()
            }
            RESTART -> {
                lemonImage?.setImageResource(R.drawable.lemon_restart)
                lemonText.setText(getString(R.string.lemon_empty_glass))
            }
        })

        // TODO: for each state, the textAction TextView should be set to the corresponding string from
        //  the string resources file. The strings are named to match the state

        // TODO: Additionally, for each state, the lemonImage should be set to the corresponding
        //  drawable from the drawable resources. The drawables have the same names as the strings
        //  but remember that they are drawables, not strings.
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * Long clicking the lemon image will show how many times the lemon has been squeezed.
     */
    private fun showSnackBar(): Boolean {
        if (lemonadeState != SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

