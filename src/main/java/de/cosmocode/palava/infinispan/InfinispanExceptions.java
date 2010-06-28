package de.cosmocode.palava.infinispan;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Tobias Sarnowski
 */
public class InfinispanExceptions extends Exception {

    private final List<Exception> exceptions;

    public InfinispanExceptions(List<Exception> exceptions) {
        super(exceptions.get(0));

        this.exceptions = exceptions;
    }

    public InfinispanExceptions(String message, List<Exception> exceptions) {
        super(message, exceptions.get(0));

        this.exceptions = exceptions;
    }


    public List<Exception> getExceptions() {
        return ImmutableList.copyOf(exceptions);
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        for (Exception e: getExceptions()) {
            message.append("\n\n");
            message.append(e.toString());
        }
        return message.toString();
    }
}