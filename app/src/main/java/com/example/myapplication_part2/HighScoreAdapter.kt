package com.example.myapplication_part2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HighScoreAdapter(
    private val scores: List<HighScore>,
    private val listener: OnHighScoreSelectedListener
) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return HighScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.nameText.text = "Player: ${score.playerName}"
        holder.scoreText.text = "Score: ${score.totalScore}"
        holder.modeText.text = "Mode: ${score.mode}"



        // לחיצה על השורה תעדכן את המפה הקטנה
        holder.itemView.setOnClickListener {
            listener.onHighScoreSelected(score.latitude, score.longitude)
        }

        // לחיצה על כפתור מיקום תפתח את המסך המלא
        holder.btnLocation.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra("latitude", score.latitude)
            intent.putExtra("longitude", score.longitude)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = scores.size

    class HighScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scoreText: TextView = itemView.findViewById(R.id.score_text)
        val nameText: TextView = itemView.findViewById(R.id.name_text)
        val modeText: TextView = itemView.findViewById(R.id.mode_text)
        val btnLocation: Button = itemView.findViewById(R.id.btn_location)
    }
}
