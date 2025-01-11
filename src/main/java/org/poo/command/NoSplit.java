package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public final class NoSplit implements Order {
    private final CommandInput command;
    private final ArrayNode output;

    public NoSplit(final CommandInput command, final ArrayNode output) {
        this.command = command;
        this.output = output;
    }

    @Override
    public void execute(final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", command.getCommand());

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("timestamp", command.getTimestamp());
        outputNode.put("description", "User not found");

        commandNode.set("output", outputNode);
        commandNode.put("timestamp", timestamp);

        output.add(commandNode);
    }
}
