// AulaStorage.java
package br.pingoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AulaStorage {
    private static final String PREF_NAME = "aulas_pref";
    private static final String KEY_AULAS = "lista_aulas";

    public static void salvarAulas(Context context, List<Aula> aulas) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(aulas);
        editor.putString(KEY_AULAS, json);
        editor.apply();
    }

    public static List<Aula> carregarAulas(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_AULAS, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Aula>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
}
