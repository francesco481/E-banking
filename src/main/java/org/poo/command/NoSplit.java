package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public class NoSplit implements Order {
    final private CommandInput command;
    final private ArrayNode output;

    public NoSplit(CommandInput command, ArrayNode output) {
        this.command = command;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
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
