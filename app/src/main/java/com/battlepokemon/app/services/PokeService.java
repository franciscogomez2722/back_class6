package com.battlepokemon.app.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.battlepokemon.app.model.pokeEntity;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class PokeService {

    @Autowired
    private FirebaseApp firebaseApp;  // inyectamos FirebaseApp con @Autowired

    private pokeEntity ultimaBatalla = new pokeEntity();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void escucharCambios() {
        Firestore db = FirestoreClient.getFirestore(firebaseApp);

        DocumentReference docRef = db.collection("pokebattles").document("battle1");

        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                ultimaBatalla = snapshot.toObject(pokeEntity.class);

                System.out.println("🔥 Cambio detectado:");
                System.out.println("Vida Pokemon 1: " + ultimaBatalla.getPokemon1Vida());
                System.out.println("Vida Pokemon 2: " + ultimaBatalla.getPokemon2Vida());

                enviarActualizacion();
            }
        });
    }

    public pokeEntity getUltimaBatalla() {
        return ultimaBatalla;
    }

    public SseEmitter agregarCliente() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    private void enviarActualizacion() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(ultimaBatalla);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}