package com.visoft.network.funcionalidades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.visoft.network.Objects.User;

import java.lang.reflect.Type;

public class GsonerUser {

    private static Gson gson;

    private GsonerUser() {

    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().registerTypeAdapter(User.class, new AbstractClassAdapter())
                    .create();
        }
        return gson;
    }

    private static class AbstractClassAdapter implements JsonSerializer<User>, JsonDeserializer<User> {

        private static final String CLASSNAME = "CLASSNAME";
        private static final String INSTANCE = "INSTANCE";

        @Override
        public JsonElement serialize(User src, Type typeOfSrc,
                                     JsonSerializationContext context) {

            JsonObject retValue = new JsonObject();
            String className = src.getClass().getName();
            retValue.addProperty(CLASSNAME, className);
            JsonElement elem = context.serialize(src);
            retValue.add(INSTANCE, elem);
            return retValue;
        }

        @Override
        public User deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
            String className = prim.getAsString();

            Class<?> klass = null;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
            return context.deserialize(jsonObject.get(INSTANCE), klass);
        }
    }
}

