package com.bimalsha.ee.bank.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoggingInterceptor {
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    @AroundInvoke
    public Object logMethodEntry(InvocationContext ctx) throws Exception {
        logger.info("Entering method: " + ctx.getMethod().getName());
        long startTime = System.currentTimeMillis();

        try {
            return ctx.proceed();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in " + ctx.getMethod().getName(), e);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Exiting method: " + ctx.getMethod().getName() +
                    " (execution time: " + executionTime + "ms)");
        }
    }
}