package com.stefano;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.stdimpl.GCacheException;
import com.google.gson.Gson;

/**
 * Author stefanofranz
 */

public class Titles extends HttpServlet {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final Cache cache = getMemcache();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        TitleEdit edit = JsonHelper.fromRequest(req, TitleEdit.class);
        if (!edit.getNewTitle().contains("<") && !edit.getNewTitle().contains(">")) {
            edit.setUUID(new BigInteger(128, new Random()).toString(36));
            writeToDatastoreAndFlushCacheIfPossible(edit);
            response.setStatus(200);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String titleId = req.getParameter("titleId");
        List<Entity> potentialTitles = loadFromCacheIfPossible(() -> {
            Query titleQuery = new Query(TitleEdit.dataStoreType())
                    .setFilter(new Query.FilterPredicate("titleId", Query.FilterOperator.EQUAL, titleId));
            PreparedQuery pq = datastore.prepare(titleQuery);
            return pq.asList(FetchOptions.Builder.withLimit(10));
        }, titleId);
        if (!potentialTitles.isEmpty()) {
            Entity result = potentialTitles.get(new Random().nextInt(potentialTitles.size()));
            new Gson().toJson(TitleEdit.fromEntity(result), TitleEdit.class, resp.getWriter());
            resp.flushBuffer();
            resp.setStatus(200);
        }
    }

    public static String getString() {
        return "";
    }

    private List<Entity> loadFromCacheIfPossible(Supplier<List<Entity>> loader, String titleId) {
        List<Entity> potentialTitles = null;
        try {
            if (cache != null && (potentialTitles = (List<Entity>) cache.get(titleId)) == null) {
                potentialTitles = loader.get();
                cache.put(titleId, potentialTitles);
            }
        } catch (GCacheException e) {
            potentialTitles = loader.get();
        }
        return potentialTitles;
    }

    private void writeToDatastoreAndFlushCacheIfPossible(TitleEdit titleEdit) {
        datastore.put(titleEdit.asEntity());
        try {
            if (cache != null) {
                cache.remove(titleEdit.getTitleId());
            }
        } catch (GCacheException ignored) {
        }
    }


    private static Cache getMemcache() {
        try {
            CacheFactory factory = CacheManager.getInstance().getCacheFactory();
            return factory.createCache(Collections.emptyMap());
        } catch (CacheException ignored) {
        }
        return null;
    }


}
