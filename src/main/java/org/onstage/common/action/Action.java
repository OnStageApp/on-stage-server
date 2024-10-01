package org.onstage.common.action;

import java.util.logging.Logger;

import static java.lang.System.nanoTime;

public interface Action<T, R> {
    default R execute(T request) {
        Logger logger = Logger.getGlobal();
        logger.info("Executing %s with input %s".formatted(getActionName(), request));
        long startTime = nanoTime();

        R response = doExecute(request);
        long endTime = nanoTime();
        long duration = endTime - startTime;

        logger.info("Successfully finished %s with response %s in %s ".formatted(getActionName(), response, duration));
        return response;
    }

    R doExecute(T request);

    default String getActionName() {
        return this.getClass().getSimpleName();
    }
}
