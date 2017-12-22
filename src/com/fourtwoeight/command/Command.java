package com.fourtwoeight.command;

public interface Command {

    /**
     * Executes the specific command
     */
    void execute();

    /**
     * Performs the actions necessary to undo the actions performed by execute.
     */
    void undo();
}
