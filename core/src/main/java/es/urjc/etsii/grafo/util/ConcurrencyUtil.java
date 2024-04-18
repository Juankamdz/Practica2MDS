package es.urjc.etsii.grafo.util;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Métodos de ayuda para tratar con tareas concurrentes
 */
public class ConcurrencyUtil {
    /**
     * Espera hasta la terminación del servicio de ejecución dado.
     * Envuelve InterruptedException en una RuntimeException sin comprobar.
     *
     * @param executor Servicio de ejecución
     */
    public static void await(ExecutorService executor) {
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            // Vuelve a lanzar la InterruptedException
            throw new RuntimeException(e);
        }
    }
    /**
     * Bloquea hasta que la tarea se complete.
     * Envuelve la molesta excepción comprobada.
     *
     * @param f Futuro por el que esperaremos
     * @param <T> Tipo de futuro
     * @return Resultado simplificado de la tarea
     * @throws java.lang.RuntimeException en caso de que ocurra algún error durante la ejecución
     */
    public static <T> T await(Future<T> f){
        try {
            return f.get();
        } catch (InterruptedException e) {
            // Re-interrumpe el hilo y relanza la excepción
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Bloquea hasta que la tarea se complete.
     * Maneja la excepción con el manipulador dado
     *
     * @param f Futuro por el que esperaremos
     * @param <T> Tipo opcional
     * @param exceptionHandler pasa la excepción al manipulador en lugar de promoverla a RuntimeException
     * @return Resultado simplificado de la tarea
     * @throws java.lang.RuntimeException en caso de que ocurra algún error durante la ejecución
     */
    public static <T> Optional<T> await(Future<T> f, Consumer<Exception> exceptionHandler){
        try {
            return Optional.of(f.get());
        } catch (InterruptedException e) {
            // Re-interrumpe el hilo y llama al manipulador de excepciones
            Thread.currentThread().interrupt();
            exceptionHandler.accept(e);
            return Optional.empty();
        } catch (ExecutionException e) {
            exceptionHandler.accept(e);
            return Optional.empty();
        }
    }
    /**
     * Espera una colección de futuros
     *
     * @param futures colección de futuros
     * @param <T> Tipo de futuros
     * @return Objetos dentro de futuros
     */
    public static <T> List<T> awaitAll(Collection<Future<T>> futures){
        return awaitAll(futures.stream());
    }
    /**
     * Espera un stream de futuros
     *
     * @param futures stream de futuros
     * @param <T> Tipo de futuros
     * @return Objetos dentro de futuros
     */
    public static <T> List<T> awaitAll(Stream<Future<T>> futures){
        return futures.map(ConcurrencyUtil::await).collect(Collectors.toList());
    }
    public static void sleep(int time, TimeUnit unit) throws InterruptedException {
        Thread.sleep(unit.toMillis(time));
    }
}