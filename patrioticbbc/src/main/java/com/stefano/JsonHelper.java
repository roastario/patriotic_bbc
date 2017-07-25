package com.stefano;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

/**
 * Author stefanofranz
 */
public class JsonHelper {


    public static <T> T fromRequest(HttpServletRequest request, Class<T> toSerialise) {
        try(Reader inputStreamReader = new InputStreamReader(request.getInputStream())){
            return new Gson().fromJson(inputStreamReader, toSerialise);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
