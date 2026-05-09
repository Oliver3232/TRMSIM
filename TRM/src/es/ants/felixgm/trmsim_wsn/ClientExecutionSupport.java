package es.ants.felixgm.trmsim_wsn;

import es.ants.felixgm.trmsim_wsn.network.Sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared helper for bounded client execution in simulation and benchmark flows.
 */
final class ClientExecutionSupport {
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);

    private ClientExecutionSupport() {
    }

    static ExecutorService createClientExecutor(int clientCount) {
        final int workers = workerCountForClientCount(clientCount);
        return Executors.newFixedThreadPool(workers, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "trmsim-client-" + THREAD_COUNTER.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    static int workerCountForClientCount(int clientCount) {
        final int availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors());
        final int desiredWorkers = Math.max(availableProcessors, availableProcessors * 2);
        return Math.max(1, Math.min(Math.max(1, clientCount), desiredWorkers));
    }

    static void executeClients(
            Collection<Sensor> clients,
            ExecutorService executorService,
            SimulationContext simulationContext) throws InterruptedException {
        List<Future<?>> futures = new ArrayList<Future<?>>(clients.size());
        for (Sensor client : clients) {
            futures.add(executorService.submit(() -> {
                if (simulationContext != null) {
                    Sensor.activateSimulationContext(simulationContext);
                }
                try {
                    client.run();
                } finally {
                    Sensor.clearActiveSimulationContext();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new IllegalStateException("Client execution failed", cause);
            }
        }
    }
}
