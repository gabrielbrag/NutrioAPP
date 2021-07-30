package br.com.nutrioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class recipeResult extends AppCompatActivity {

    TextView totalCal;
    TextView totalCalFat;
    TextView totalFat;
    TextView totalCol;
    TextView totalSodium;
    TextView totalPot;
    TextView totalCarbo;
    TextView totalProt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_result);

        Bundle b = getIntent().getExtras();
        totalCal = (TextView)findViewById(R.id.viewTotalCal);
        totalFat = (TextView) findViewById(R.id.viewFat);
        totalCol = (TextView) findViewById(R.id.viewColest);
        totalSodium = (TextView) findViewById(R.id.viewSodium);
        totalPot = (TextView) findViewById(R.id.viewPot);
        totalCarbo = (TextView) findViewById(R.id.viewCarbo);
        totalProt = (TextView) findViewById(R.id.viewProt);

        totalCal.setText(String.format("Calorias: %.2f", b.getDouble("nf_calories")));
        totalFat.setText(String.format("Gorduras totais %.2fg", b.getDouble("nf_total_fat")));
        totalCol.setText(String.format("Colesterol: %.2fmg", b.getDouble("nf_cholesterol")));
        totalSodium.setText(String.format("Sódio: %.2fmg", b.getDouble("nf_sodium")));
        totalPot.setText(String.format("Potássio: %.2fmg", b.getDouble("nf_potassium")));
        totalCarbo.setText(String.format("Carboidratos %.2fg", b.getDouble("nf_total_carbohydrate")));
        totalProt.setText(String.format("Proteínas: %.2fg", b.getDouble("nf_protein")));
    }
}