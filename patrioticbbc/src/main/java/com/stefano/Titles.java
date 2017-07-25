package com.stefano;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

/**
 * Author stefanofranz
 */

public class Titles extends HttpServlet {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        TitleEdit edit = JsonHelper.fromRequest(req, TitleEdit.class);
        edit.setUUID(new BigInteger(128, new Random()).toString(36));
        datastore.put(edit.asEntity());
        response.setStatus(200);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String titleId = req.getParameter("titleId");
        Query titleQuery = new Query(TitleEdit.dataStoreType())
                .setFilter(new Query.FilterPredicate("titleId", Query.FilterOperator.EQUAL, titleId));
        PreparedQuery pq = datastore.prepare(titleQuery);
        Entity result = pq.asIterable().iterator().next();
        new Gson().toJson(TitleEdit.fromEntity(result), TitleEdit.class, resp.getWriter());
        resp.flushBuffer();
        resp.setStatus(200);
    }

    public static String getString() {
        return "";
    }


}
