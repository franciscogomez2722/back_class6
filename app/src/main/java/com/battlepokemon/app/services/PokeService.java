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

/*
Escucha cambios en Firestore automáticamente
Guarda el último estado de la batalla
Notifica a todos los clientes conectados (frontend) en tiempo real
 */


@Service
public class PokeService {

    @Autowired
    private FirebaseApp firebaseApp;  // inyectamos FirebaseApp con @Autowired

    private pokeEntity ultimaBatalla = new pokeEntity();

    // Lista de clientes conectados (navegadores, apps, etc.)
    // CopyOnWriteArrayList es segura para múltiples hilos (muy importante aquí)
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Este método se ejecuta automáticamente cuando inicia la aplicación
    @PostConstruct
    public void escucharCambios() {
        Firestore db = FirestoreClient.getFirestore(firebaseApp);

        // Apunta al documento: pokebattles/battle1
        DocumentReference docRef = db.collection("pokebattles").document("battle1");

        // Listener en tiempo real:
        // Este bloque se ejecuta cada vez que el documento cambia en Firestore
        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }

            // Convierte el documento de Firestore (JSON) a objeto Java
            // y actualiza el estado en memoria
            if (snapshot != null && snapshot.exists()) {
                ultimaBatalla = snapshot.toObject(pokeEntity.class);

                System.out.println("Cambio detectado:");
                System.out.println("Vida Pokemon 1: " + ultimaBatalla.getPokemon1Vida());
                System.out.println("Vida Pokemon 2: " + ultimaBatalla.getPokemon2Vida());

                // Notifica a todos los clientes conectados (SSE)
                enviarActualizacion();
            }
        });
    }

    public pokeEntity getUltimaBatalla() {
        return ultimaBatalla;
    }

    // Método que registra un nuevo cliente que quiere recibir actualizaciones
    public SseEmitter agregarCliente() {

        // Crea una conexión SSE (Server-Sent Events)
        // Long.MAX_VALUE = conexión prácticamente infinita (no expira)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Guarda el cliente en la lista de suscriptores
        emitters.add(emitter);

        // Cuando el cliente se desconecta, se elimina de la lista
        emitter.onCompletion(() -> emitters.remove(emitter));

        // Si la conexión expira, también se elimina
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    // Envía datos a todos los clientes conectados
    private void enviarActualizacion() {

        // Envía el objeto actualizado al cliente (JSON automáticamente)
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(ultimaBatalla);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}

/*

Firestore = sensor 🔔
Listener = alarma 🚨
SSE = altavoz 📢
Cliente = oyente 👂
*/