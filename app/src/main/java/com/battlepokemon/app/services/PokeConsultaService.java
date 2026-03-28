package com.battlepokemon.app.services;

import com.battlepokemon.app.model.pokeEntity;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class PokeConsultaService {

    public pokeEntity getVidaPokemons() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        // .get() devuelve un ApiFuture<DocumentSnapshot>
        // Es decir: una promesa de que el documento llegará en el futuro
        DocumentSnapshot docSnapshot = db.collection("pokebattles")
                                        .document("battle1")
                                        .get()
                                        .get(); // get() bloquea hasta obtener el documento

        // Verifica si el documento existe en la base de datos
        if (docSnapshot.exists()) {
            return docSnapshot.toObject(pokeEntity.class);
        } else {
            return new pokeEntity(); // devolver valores por defecto si no existe
        }
    }
}