package org.poo.command;

/**
 * Represents a command in the Command design pattern.
 * Each implementation of this interface encapsulates an action
 * (or command) that can be executed, typically within the context
 * of a system requiring undoable or loggable operations.
 */
public interface Order {

    /**
     * Executes the command with the provided timestamp.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  This can be used for logging or tracking purposes.
     */
    void execute(int timestamp);
}
