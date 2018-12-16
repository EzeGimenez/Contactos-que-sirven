package com.visoft.network.Util;

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
import com.visoft.network.Objects.Message;

import java.lang.reflect.Type;

public class Gsoner {
    private static Gson gson;

    private Gsoner() {

    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().registerTypeAdapter(Message.class, new InterfaceAdapter())
                    .create();
        }
        return gson;
    }

    private static class InterfaceAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

        private static final String CLASSNAME = "CLASSNAME";
        private static final String INSTANCE = "INSTANCE";

        @Override
        public JsonElement serialize(Message src, Type typeOfSrc,
                                     JsonSerializationContext context) {

            JsonObject retValue = new JsonObject();
            String className = src.getClass().getName();
            retValue.addProperty(CLASSNAME, className);
            JsonElement elem = context.serialize(src);
            retValue.add(INSTANCE, elem);
            return retValue;
        }

        @Override
        public Message deserialize(JsonElement json, Type typeOfT,
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
