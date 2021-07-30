package br.com.nutrioapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.btnSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRecipe recipe = new searchRecipe();
                recipe.execute("https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=pt&to=en");
            }
        });
    }

    protected class searchRecipe extends AsyncTask<String, Void, String> {
        private URL requestURL;
        private String ingredients;

        @Override
        protected String doInBackground(String... strings) {
            try {
                this.requestURL = new URL((strings[0]));
                HttpURLConnection con = (HttpURLConnection) this.requestURL.openConnection();
                con.setRequestMethod("POST");
                String text = edit.getText().toString();

                if (ingredients == null) {
                    con.setRequestProperty("Ocp-Apim-Subscription-Key", "b1ad95bf4bac4ba28632e9a90d547810");
                    con.setRequestProperty("Ocp-Apim-Subscription-Region", "brazilsouth");
                    con.setRequestProperty("Content-Type", "application/json");

                    text = text.replaceAll("\n", "\\\\n");
//                osw.write("{\"text\":\"" + text + "\"}");
                }else {
                    con.setRequestProperty("x-app-id", "d423c79b");
                    con.setRequestProperty("x-app-key", "7006499131ee72fd9fa628adbb72b5f2");
                    con.setRequestProperty("Content-Type", "application/json");

                    //text = text.replaceAll("\n","\\\\n");
                }
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                System.out.println("{\"query\":\"" + this.ingredients + "\"}");

                osw.write(ingredients == null ? "[{\"text\":\"" + text + "\" }]" : "{\"query\":\"" + this.ingredients + "\"}");
                osw.flush();
                osw.close();
                os.close();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer out = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    out.append(line + "\n");
                }
                is.close();
                return out.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPostExecute(String jsonReturn) {
            try {
                if(this.ingredients == null){
                    JSONArray array = new JSONArray(jsonReturn);
                    System.out.println("Tradução " + array.toString());
                    JSONObject jsonObj = array.getJSONObject(0);
                    searchRecipe recipe = new searchRecipe();
                    recipe.ingredients = jsonObj.getJSONArray("translations").getJSONObject(0).getString("text").replaceAll("\n", "\\\\n");
                    recipe.execute("https://trackapi.nutritionix.com/v2/natural/nutrients");
                }else{
                    JSONObject json = parseJSON(jsonReturn);
                    System.out.println("Ingredientes " + json.toString());
                    JSONArray foods = json.getJSONArray("foods");

                    double totalCal = 0;
                    double totalFat = 0;
                    double cholesterol = 0;
                    double sodium = 0;
                    double potassium = 0;
                    double carbohydatres = 0;
                    double protein = 0;

                    for(int i = 0; i < foods.length(); i++){
                        System.out.println(foods.getJSONObject(i).toString());
                        totalCal += foods.getJSONObject(i).getDouble("nf_calories");
                        totalFat += foods.getJSONObject(i).getDouble("nf_total_fat");
                        cholesterol += foods.getJSONObject(i).getDouble("nf_cholesterol");
                        sodium += foods.getJSONObject(i).getDouble("nf_sodium");
                        potassium += foods.getJSONObject(i).getDouble("nf_potassium");
                        carbohydatres += foods.getJSONObject(i).getDouble("nf_total_carbohydrate");
                        protein += foods.getJSONObject(i).getDouble("nf_protein");
                    }

                    Intent intent = new Intent(MainActivity.this, recipeResult.class);
                    intent.putExtra("nf_calories", totalCal);
                    intent.putExtra("nf_total_fat", totalFat);
                    intent.putExtra("nf_cholesterol", cholesterol);
                    intent.putExtra("nf_sodium", sodium);
                    intent.putExtra("nf_potassium", potassium);
                    intent.putExtra("nf_total_carbohydrate", carbohydatres);
                    intent.putExtra("nf_protein", protein);
                    startActivity(intent);
                }
//

            }catch (Exception e){
            }
        }

        //O método abaixo serve para ler o JSON recebido e processar os itens contidos no mesmo
        private JSONObject parseJSON(String data){
            try{
                JSONObject jsonObject = new JSONObject(data);//JSONObject recebe o JSON da consulta
                //Depois de pegar o conteúdo no JSON, preenche os textview
                return jsonObject;
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                return null;
            }
        }
    }
}