package me.gabriel.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Adaptador para Gson que serializa e desserializa objetos java.time.LocalDate.
 * Converte LocalDate para uma string no formato ISO-8601 (ex: "2023-10-27") e vice-versa.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDate.toString());
        }
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            return LocalDate.parse(jsonReader.nextString());
        }
    }
}
