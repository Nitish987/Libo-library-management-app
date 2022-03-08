package com.nk.libo.db;

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;;
import com.nk.libo.utils.Assure;

import java.util.Map;

public class Database {
    public Database() {
    }

    public static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }

    public static <T> void create(String path, T data, Assure assure) {
        try {
            getInstance().document(path).set(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) assure.accept(data);
                else assure.reject("Unable to create");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static void exists(String path, Object object, Assure assure) {
        try {
            getInstance().document(path).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) assure.accept(task.getResult().toObject(object.getClass()));
                else assure.reject("unable to get the data");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static void read(String path, Object object, Assure assure) {
        try {
            getInstance().document(path).get().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    assure.accept(task.getResult().toObject(object.getClass()));
                else assure.reject("Unable to read");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static void update(String path, Map<String, Object> data, Assure assure) {
        try {
            getInstance().document(path).update(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) assure.accept(true);
                else assure.reject("Unable to update");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static void delete(String path, Assure assure) {
        try {
            getInstance().document(path).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) assure.accept(true);
                else assure.reject("unable to delete");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static void readCollection(Query query, Assure assure) {
        try {
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) assure.accept(task.getResult().getDocuments());
                else assure.reject("Unable to read collection");
            });
        } catch (Exception e) {
            assure.reject(e.getMessage());
        }
    }

    public static class Storage {
        private static StorageReference reference = FirebaseStorage.getInstance().getReference();

        public Storage() {
        }

        public static FirebaseStorage getInstance() {
            return FirebaseStorage.getInstance();
        }

        public static void put(String path, String filename, Uri uri, Assure assure) {
            try {
                path = path + "/" + filename;
                StorageReference storage = reference.child(path);
                storage.putFile(uri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) storage.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) assure.accept(task1.getResult());
                        else assure.reject("unable to get download Url");
                    });
                    else assure.reject("unable to upload file");
                });
            } catch (Exception e) {
                assure.reject(e.getMessage());
            }
        }
    }
}























